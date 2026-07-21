package com.example.demo.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

/**
 * 统一 OpenAPI 配置 — 所有服务共用，将文档中的 server 地址指向网关，添加 JWT 认证。
 * <p>
 * 通过 {@link GlobalOperationCustomizer} 给每个接口添加 Authorization 输入框，
 * 解决 Knife4j 4.3.0 不渲染 OpenAPI security scheme 锁图标的问题。
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "BearerAuth";

    @Value("${springdoc.server-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url(serverUrl).description("API 网关"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("先调用 认证服务 → 登录 接口获取 Token，粘贴到这里")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }

    /**
     * 给每个接口都加上 Authorization 输入框 —— Knife4j 调试面板里直接填 token
     */
    @Bean
    public GlobalOperationCustomizer globalHeaderCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> operation
                .addParametersItem(new HeaderParameter()
                        .name("Authorization")
                        .description("Bearer <token>（先调登录接口获取）")
                        .required(false));
    }
}
