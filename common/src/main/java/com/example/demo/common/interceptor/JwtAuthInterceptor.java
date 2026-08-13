package com.example.demo.common.interceptor;

import com.example.demo.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 通用 JWT 认证拦截器（所有业务服务共用）
 * <p>
 * 职责：校验 Token 签名和有效期，将用户信息注入 request attributes。
 * <b>不做授权判断</b>——授权由 {@code @RequirePermission} + {@code PermissionAspect} 负责。
 * </p>
 */
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期，请重新登录\"}");
            return false;
        }

        // 注入用户信息到 request attributes，供后续 PermissionAspect 使用
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("studentId", jwtUtil.getStudentIdFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        request.setAttribute("permissions", jwtUtil.getPermissionsFromToken(token));
        return true;
    }
}
