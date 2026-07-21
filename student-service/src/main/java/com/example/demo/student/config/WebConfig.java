package com.example.demo.student.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * 注册 JWT 拦截器，配置拦截 / 放行路径。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")                         // 拦截所有请求
                .excludePathPatterns(                           // 放行以下路径（无需 Token）
                        "/",                                    // 首页
                        "/index.html",                          // 首页
                        "/static/**",                           // 静态资源
                        "/favicon.ico",                         // 网站图标
                        "/auth/**",                             // 注册/登录/登出
                        "/error",                               // Spring Boot 错误页
                        "/kafka/**",                            // Kafka 测试接口
                        "/doc.html",                            // Knife4j 文档
                        "/webjars/**",                          // Knife4j 静态资源
                        "/v3/**"                                // OpenAPI 规范
                );
    }
}
