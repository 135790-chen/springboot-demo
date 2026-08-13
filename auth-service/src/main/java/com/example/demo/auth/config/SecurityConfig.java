package com.example.demo.auth.config;

import com.example.demo.common.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置（认证服务）
 * <p>
 * 集成 JWT + RBAC：
 * <ul>
 *   <li>{@link JwtAuthenticationFilter} — 从请求头提取 JWT，设置 SecurityContext</li>
 *   <li>{@code @EnableMethodSecurity} — 支持 {@code @PreAuthorize("hasAuthority('course:manage')")}</li>
 *   <li>URL 级别的访问控制由 {@code JwtAuthInterceptor}（WebConfig）负责</li>
 *   <li>Spring Security 负责方法级权限检查（@PreAuthorize）</li>
 * </ul>
 * <p>
 * 双保险设计：JwtAuthInterceptor（MVC 拦截器） + JwtAuthenticationFilter（Security 过滤器）
 * 两者互不冲突，各自独立工作。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 所有请求在 URL 层面放行（由 JwtAuthInterceptor 做 URL 级控制）
                        // 方法级权限由 @PreAuthorize / @RequirePermission 负责
                        .anyRequest().permitAll()
                )
                // 在 Spring Security 过滤器链中插入 JWT 过滤器
                // 这样 SecurityContext 会被自动填充，@PreAuthorize 才能正常工作
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
