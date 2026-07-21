package com.example.demo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * <p>
 * 关闭默认的 Security 过滤器链，只用 BCryptPasswordEncoder。
 * JWT 鉴权由自定义的 JwtInterceptor 处理。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt 密码编码器（Spring 管理的单例 Bean）
     * <p>
     * 强度为 10，表示 2^10 轮哈希迭代。
     * 所有需要密码加密的地方统一注入此 Bean，避免多处创建。
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)          // 关闭 CSRF（REST API 不需要）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()               // 所有请求放行，由 JwtInterceptor 控制
                );
        return http.build();
    }
}
