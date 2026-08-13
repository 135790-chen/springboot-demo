package com.example.demo.statistics.config;

import com.example.demo.common.JwtUtil;
import com.example.demo.common.interceptor.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * <p>
 * 注册通用 JWT 认证拦截器（来自 common 模块），配置拦截 / 放行路径。
 * 授权由 @RequirePermission + PermissionAspect 负责。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtil))
                .addPathPatterns("/**")                         // 拦截所有请求
                .excludePathPatterns(                           // 放行以下路径（无需 Token）
                        "/",                                    // 首页
                        "/index.html",                          // 首页
                        "/static/**",                           // 静态资源
                        "/favicon.ico",                         // 网站图标
                        "/error",                               // Spring Boot 错误页
                        "/doc.html",                            // Knife4j 文档
                        "/webjars/**",                          // Knife4j 静态资源
                        "/v3/**"                                // OpenAPI 规范
                );
    }
}
