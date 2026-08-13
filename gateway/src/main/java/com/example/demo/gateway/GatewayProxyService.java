package com.example.demo.gateway;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

/**
 * 网关代理服务 —— 封装下游微服务调用，受 Sentinel 保护
 *
 * <p>通过 {@link SentinelResource} 为每次代理请求创建 Sentinel 资源 "gateway_proxy"，
 * 支持流控、熔断和降级。当 Sentinel 触发限流或熔断时，自动调用对应的 fallback/blockHandler 方法。</p>
 */
@Component
public class GatewayProxyService {

    private static final Logger log = LoggerFactory.getLogger(GatewayProxyService.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 代理请求到下游微服务（受 Sentinel 保护）
     *
     * @param request    原始 HTTP 请求
     * @param body       请求体（可能为 null）
     * @param targetBase 目标服务 base URL（如 http://student-service）
     * @return 下游响应
     */
    @SentinelResource(
            value = "gateway_proxy",
            fallback = "proxyFallback",
            blockHandler = "proxyBlocked"
    )
    public ResponseEntity<String> proxy(HttpServletRequest request, String body, String targetBase) {
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
            } catch (IOException ignored) {
            }
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpEntity<String> entity = body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(targetUrl, method, entity, String.class);
        HttpHeaders cleanHeaders = new HttpHeaders();
        resp.getHeaders().forEach((key, values) -> {
            String lower = key.toLowerCase();
            if (!"transfer-encoding".equals(lower) && !"connection".equals(lower)) {
                cleanHeaders.put(key, values);
            }
        });
        return ResponseEntity.status(resp.getStatusCode())
                .headers(cleanHeaders)
                .body(resp.getBody());
    }

    /**
     * Fallback — 下游服务调用异常时（未熔断但实际出错）的降级处理
     */
    public ResponseEntity<String> proxyFallback(HttpServletRequest request, String body,
                                                String targetBase, Throwable ex) {
        String serviceName = targetBase.replace("http://", "");
        log.error("网关代理调用异常 [{}]: {}", serviceName, ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(headers)
                .body("{\"code\":503,\"message\":\"" + serviceName + " 服务暂时不可用，请稍后重试\"}");
    }

    /**
     * BlockHandler — Sentinel 流控/熔断触发时（电路打开或 QPS 超限）
     */
    public ResponseEntity<String> proxyBlocked(HttpServletRequest request, String body,
                                               String targetBase, BlockException ex) {
        String serviceName = targetBase.replace("http://", "");
        log.warn("Sentinel 限流/熔断 [{}]: rule={}", serviceName, ex.getRule());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(429)
                .headers(headers)
                .body("{\"code\":429,\"message\":\"系统繁忙，请稍后重试\"}");
    }

    /**
     * 代理 Knife4j 文档请求（简单路径重写，不走 Sentinel 保护链路）
     */
    public ResponseEntity<String> proxyDocs(HttpServletRequest request, String targetBase, String prefix) {
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
}
