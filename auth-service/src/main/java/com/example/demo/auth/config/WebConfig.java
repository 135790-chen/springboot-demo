package com.example.demo.auth.config;

import com.example.demo.common.JwtUtil;
import com.example.demo.common.interceptor.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置（认证服务）
 * <p>
 * 为 RBAC 管理接口注册 JWT 认证拦截器。
 * /auth/** 公开端点（注册/登录/登出）不受拦截。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor(jwtUtil))
                .addPathPatterns("/api/rbac/**")         // RBAC 管理接口需要认证
                .addPathPatterns("/auth/me")              // 当前用户信息需要认证
                .addPathPatterns("/auth/account")         // 注销账号需要认证
                .excludePathPatterns("/auth/login", "/auth/register");  // 登录注册公开
    }
}
