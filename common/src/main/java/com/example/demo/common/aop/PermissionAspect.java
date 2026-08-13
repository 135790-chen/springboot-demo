package com.example.demo.common.aop;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 权限校验切面 — 从 JwtInterceptor 注入的 request attributes 中读取权限列表，
 * 与 @RequirePermission 注解的 value 匹配，无权限返回 403。
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);

    @Around("@annotation(perm)")
    public Object around(ProceedingJoinPoint p, RequirePermission perm) throws Throwable {
        String required = perm.value();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            log.warn("[PermissionAspect] 无法获取请求上下文，放行");
            return p.proceed();
        }

        HttpServletRequest request = attrs.getRequest();

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) request.getAttribute("permissions");

        if (permissions == null || permissions.isEmpty()) {
            log.warn("[PermissionAspect] Token 中无权限列表，拒绝访问: required={}", required);
            return Result.error(403, "权限不足：未分配任何权限");
        }

        if (!permissions.contains(required)) {
            log.warn("[PermissionAspect] 权限不足: required={}, userPermissions={}", required, permissions);
            return Result.error(403, "权限不足：需要 [" + required + "] 权限");
        }

        return p.proceed();
    }
}
