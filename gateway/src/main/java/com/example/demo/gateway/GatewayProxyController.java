package com.example.demo.gateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;

/**
 * API 网关代理 —— 通过 Nacos 服务发现转发到对应微服务
 * /auth/**    → auth-service
 * /students/** → student-service
 * /api/edu/** → student-service
 * /kafka/**   → message-service
 */
@RestController
public class GatewayProxyController {

    @Configuration
    public static class RestTemplateConfig {
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            // 用 Apache HttpClient 5 替代默认 HttpURLConnection，避免 POST 代理时 chunked encoding 的问题
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            // 不抛异常，让上游的 4xx/5xx 原样透传回客户端
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) { return false; }
                @Override
                public void handleError(ClientHttpResponse response) {}
            });
            return restTemplate;
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Knife4jConfig knife4jConfig;

    // ========== 首页 ==========

    @GetMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html");
    }

    // ========== 业务代理 ==========

    @RequestMapping(value = "/auth/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyAuth(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxy(request, body, "http://auth-service");
    }

    @RequestMapping(value = "/students/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyStudent(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxy(request, body, "http://student-service");
    }

    @RequestMapping(value = "/api/edu/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEdu(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxy(request, body, "http://student-service");
    }

    @RequestMapping(value = "/kafka/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyKafka(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxy(request, body, "http://message-service");
    }

    // ========== Knife4j 文档聚合 ==========

    /**
     * 返回文档分组列表，Knife4j 前端通过 /v3/api-docs/swagger-config 调用
     */
    @GetMapping("/v3/api-docs/swagger-config")
    public ResponseEntity<?> swaggerConfig() {
        List<Knife4jConfig.SwaggerResource> resources = knife4jConfig.swaggerResources();
        String json = "{"
                + "\"configUrl\":\"/v3/api-docs/swagger-config\","
                + "\"oauth2RedirectUrl\":\"\","
                + "\"urls\":" + toJsonArray(resources) + ","
                + "\"validatorUrl\":\"\","
                + "\"deepLinking\":true,"
                + "\"displayOperationId\":false,"
                + "\"defaultModelsExpandDepth\":1,"
                + "\"defaultModelExpandDepth\":1,"
                + "\"defaultModelRendering\":\"model\","
                + "\"displayRequestDuration\":false,"
                + "\"docExpansion\":\"list\","
                + "\"showExtensions\":false,"
                + "\"showCommonExtensions\":false,"
                + "\"supportedSubmitMethods\":[\"get\",\"post\",\"put\",\"delete\"],"
                + "\"csrf\":{\"enabled\":false},"
                + "\"urls.primaryName\":\"学生服务\""
                + "}";
        return ResponseEntity.ok(json);
    }

    private String toJsonArray(List<Knife4jConfig.SwaggerResource> resources) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i > 0) sb.append(",");
            Knife4jConfig.SwaggerResource r = resources.get(i);
            sb.append("{\"name\":\"").append(r.getName())
              .append("\",\"url\":\"").append(r.getUrl())
              .append("\",\"swaggerVersion\":\"").append(r.getSwaggerVersion())
              .append("\",\"location\":\"").append(r.getUrl()).append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    // ========== 文档代理（各服务的 /v3/api-docs） ==========

    @RequestMapping(value = {"/v3/api-docs-auth", "/v3/api-docs-auth/**"})
    public ResponseEntity<?> proxyAuthDocs(HttpServletRequest request) {
        return proxyDocs(request, "http://auth-service", "/v3/api-docs-auth");
    }

    @RequestMapping(value = {"/v3/api-docs-student", "/v3/api-docs-student/**"})
    public ResponseEntity<?> proxyStudentDocs(HttpServletRequest request) {
        return proxyDocs(request, "http://student-service", "/v3/api-docs-student");
    }

    @RequestMapping(value = {"/v3/api-docs-message", "/v3/api-docs-message/**"})
    public ResponseEntity<?> proxyMessageDocs(HttpServletRequest request) {
        return proxyDocs(request, "http://message-service", "/v3/api-docs-message");
    }

    private ResponseEntity<?> proxyDocs(HttpServletRequest request, String targetBase, String prefix) {
        try {
            String path = request.getRequestURI().replace(prefix, "/v3/api-docs");
            if (request.getQueryString() != null) {
                path += "?" + request.getQueryString();
            }
            return restTemplate.getForEntity(targetBase + path, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"code\":500,\"message\":\"文档加载失败: " + e.getMessage() + "\"}");
        }
    }

    private ResponseEntity<?> proxy(HttpServletRequest request, String body, String targetBase) {
        String path = request.getRequestURI();
        String query = request.getQueryString();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(targetBase + path);
        if (query != null) {
            builder.query(query);
        }
        URI targetUrl = builder.build(true).toUri();

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String lower = name.toLowerCase();
            if (!"host".equals(lower) && !"content-length".equals(lower) && !"transfer-encoding".equals(lower)) {
                headers.add(name, request.getHeader(name));
            }
        }

        // 如果 @RequestBody 没读到 body，从流中兜底读取
        if (body == null && ("POST".equalsIgnoreCase(request.getMethod())
                || "PUT".equalsIgnoreCase(request.getMethod())
                || "PATCH".equalsIgnoreCase(request.getMethod()))) {
            try {
                var inputStream = request.getInputStream();
                if (inputStream != null) {
                    byte[] bytes = inputStream.readAllBytes();
                    if (bytes.length > 0) {
                        body = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                    }
                }
            } catch (IOException ignored) {}
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpEntity<String> entity = body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(targetUrl, method, entity, String.class);
            // 确保错误响应也正常返回给前端
            return ResponseEntity.status(resp.getStatusCode())
                    .headers(resp.getHeaders())
                    .body(resp.getBody());
        } catch (Exception e) {
            HttpHeaders errHeaders = new HttpHeaders();
            errHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .headers(errHeaders)
                    .body("{\"code\":502,\"message\":\"网关转发失败[" + targetBase + "]: " + e.getMessage() + "\"}");
        }
    }
}
