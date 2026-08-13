package com.example.demo.teaching.config;

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
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/static/**",
                        "/favicon.ico",
                        "/auth/**",
                        "/error",
                        "/kafka/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/**",
                        "/api/edu/student-course/confirm",
                        "/api/edu/student-course/seckill"
                );
    }
}
