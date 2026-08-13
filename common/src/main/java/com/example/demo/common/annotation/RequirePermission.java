package com.example.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解 — 标注在 Controller 方法上，指定所需权限编码
 * <pre>
 *   &#064;RequirePermission("course:edit")
 *   &#064;PostMapping("/course")
 *   public Result<?> addCourse(...) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value();
}
