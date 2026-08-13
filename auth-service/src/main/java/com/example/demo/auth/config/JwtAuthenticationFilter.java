package com.example.demo.auth.config;

import com.example.demo.common.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器 — 从请求头提取 JWT Token，校验后设置 Spring Security 上下文。
 * <p>
 * 仅做认证（Authentication），不做授权（Authorization）。
 * 授权由 {@code @PreAuthorize} 或 {@code @RequirePermission} 负责。
 * <p>
 * 对于无 Token 或 Token 无效的请求，不设置 SecurityContext，交由后续安全配置处理。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                List<String> permissions = jwtUtil.getPermissionsFromToken(token);

                JwtAuthenticationToken authentication =
                        new JwtAuthenticationToken(userId, username, permissions);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[JwtAuthFilter] 用户已认证: userId={}, permissions={}", userId, permissions);
            } else {
                log.debug("[JwtAuthFilter] Token 无效或已过期");
            }
        }

        filterChain.doFilter(request, response);
    }
}
