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

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * API 网关代理 —— 通过 Nacos 服务发现转发到对应微服务
 * /auth/**    → auth-service
 * /students/** → student-service
 * /api/edu/** → student-service（兜底）/ organization-service / teaching-service / statistics-service
 * /kafka/**   → message-service
 */
@RestController
public class GatewayProxyController {

    private static final Logger log = LoggerFactory.getLogger(GatewayProxyController.class);

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
    private GatewayProxyService proxyService;

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
        return proxyService.proxy(request, body, "http://auth-service");
    }

    @RequestMapping(value = "/students/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyStudent(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://student-service");
    }

    // ========== /api/edu/* 子路径分流 ==========
    // 注意：Spring MVC 匹配更具体的 pattern 优先，这些必须写在 /api/edu/** 前面

    @RequestMapping(value = "/api/edu/college/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduCollege(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://organization-service");
    }

    @RequestMapping(value = "/api/edu/major/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduMajor(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://organization-service");
    }

    @RequestMapping(value = "/api/edu/class/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduClass(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://organization-service");
    }

    @RequestMapping(value = "/api/edu/course/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduCourse(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/teacher/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduTeacher(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/student-course/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduEnrollment(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/graduation/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduGraduation(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/training-plan/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduTrainingPlan(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/stat/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduStat(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://statistics-service");
    }

    @RequestMapping(value = "/api/edu/classroom/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduClassroom(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/timeslot/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduTimeslot(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/api/edu/schedule/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEduSchedule(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    // /api/edu/student/** 和其他未匹配的 /api/edu/** → student-service（兜底）
    @RequestMapping(value = "/api/edu/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyEdu(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://student-service");
    }

    @RequestMapping(value = "/kafka/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyKafka(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://message-service");
    }

    @RequestMapping(value = "/organization/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyOrganization(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://organization-service");
    }

    @RequestMapping(value = "/teaching/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyTeaching(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://teaching-service");
    }

    @RequestMapping(value = "/statistics/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyStatistics(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://statistics-service");
    }

    @RequestMapping(value = "/api/leader/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyLeader(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxy(request, body, "http://statistics-service");
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
        return proxyService.proxyDocs(request, "http://auth-service", "/v3/api-docs-auth");
    }

    @RequestMapping(value = {"/v3/api-docs-student", "/v3/api-docs-student/**"})
    public ResponseEntity<?> proxyStudentDocs(HttpServletRequest request) {
        return proxyService.proxyDocs(request, "http://student-service", "/v3/api-docs-student");
    }

    @RequestMapping(value = {"/v3/api-docs-message", "/v3/api-docs-message/**"})
    public ResponseEntity<?> proxyMessageDocs(HttpServletRequest request) {
        return proxyService.proxyDocs(request, "http://message-service", "/v3/api-docs-message");
    }

    @RequestMapping(value = {"/v3/api-docs-organization", "/v3/api-docs-organization/**"})
    public ResponseEntity<?> proxyOrganizationDocs(HttpServletRequest request) {
        return proxyService.proxyDocs(request, "http://organization-service", "/v3/api-docs-organization");
    }

    @RequestMapping(value = {"/v3/api-docs-teaching", "/v3/api-docs-teaching/**"})
    public ResponseEntity<?> proxyTeachingDocs(HttpServletRequest request) {
        return proxyService.proxyDocs(request, "http://teaching-service", "/v3/api-docs-teaching");
    }

    @RequestMapping(value = {"/v3/api-docs-statistics", "/v3/api-docs-statistics/**"})
    public ResponseEntity<?> proxyStatisticsDocs(HttpServletRequest request) {
        return proxyService.proxyDocs(request, "http://statistics-service", "/v3/api-docs-statistics");
    }
}
