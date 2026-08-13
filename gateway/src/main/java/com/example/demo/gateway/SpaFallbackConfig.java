package com.example.demo.gateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * SPA fallback：对于非 API、非静态资源的请求，返回 index.html
 * 让 Vue Router 处理客户端路由（/dashboard, /students, /classes 等）
 *
 * <p>Spring MVC 的匹配优先级：Controller 方法 > ResourceHandler。
 * 所以 API 代理（/auth/**, /students/**, /api/edu/** 等）仍然由
 * {@link GatewayProxyController} 处理，不会受影响。</p>
 */
@Configuration
public class SpaFallbackConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        // 如果静态文件存在，直接返回
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        // 不存在则 fallback 到 index.html（SPA 路由）
                        Resource fallback = location.createRelative("index.html");
                        if (fallback.exists() && fallback.isReadable()) {
                            return fallback;
                        }
                        return null;
                    }
                });
    }
}
