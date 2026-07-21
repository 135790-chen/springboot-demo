package com.example.demo.gateway;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Knife4j 文档聚合 —— 将三个服务的 OpenAPI 文档聚合到一个页面
 */
@Configuration
public class Knife4jConfig {

    /**
     * 提供文档分组列表，Knife4j 的 /v3/api-docs/swagger-config 会调到这里
     */
    public List<SwaggerResource> swaggerResources() {
        List<SwaggerResource> resources = new ArrayList<>();
        resources.add(new SwaggerResource("认证服务", "/v3/api-docs-auth", "2.0"));
        resources.add(new SwaggerResource("学生服务", "/v3/api-docs-student", "2.0"));
        resources.add(new SwaggerResource("消息服务", "/v3/api-docs-message", "2.0"));
        return resources;
    }

    public static class SwaggerResource {
        private String name;
        private String url;
        private String swaggerVersion;

        public SwaggerResource(String name, String url, String swaggerVersion) {
            this.name = name;
            this.url = url;
            this.swaggerVersion = swaggerVersion;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getSwaggerVersion() { return swaggerVersion; }
        public void setSwaggerVersion(String swaggerVersion) { this.swaggerVersion = swaggerVersion; }
    }
}
