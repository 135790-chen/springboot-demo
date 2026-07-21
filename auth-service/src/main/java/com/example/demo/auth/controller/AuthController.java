package com.example.demo.auth.controller;

import com.example.demo.common.Result;
import com.example.demo.common.dto.LoginRequest;
import com.example.demo.common.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理", description = "用户注册、登录、登出、获取当前用户信息")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return Result.success(user);
    }

    @Operation(summary = "用户登录", description = "返回 JWT Token")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return Result.success(Map.of("token", token));
    }

    @Operation(summary = "用户登出", description = "Token 加入黑名单")
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(400, "缺少有效的 Token");
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return Result.success("已退出登录");
    }

    @Operation(summary = "注销账号", description = "永久删除当前用户，需携带 Token")
    @DeleteMapping("/account")
    public Result<String> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录，请先登录");
        }
        String token = authHeader.substring(7);
        authService.deleteAccount(token);
        return Result.success("账号已注销");
    }

    @Operation(summary = "获取当前用户信息", description = "需携带 Token，检查 Redis 活跃状态")
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录，请先登录");
        }
        String token = authHeader.substring(7);
        Map<String, Object> info = authService.getCurrentUser(token);
        return Result.success(info);
    }
}
