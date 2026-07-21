package com.example.demo.auth.service;

import com.example.demo.common.dto.LoginRequest;
import com.example.demo.common.dto.RegisterRequest;
import com.example.demo.entity.User;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param req 注册请求（含 username / password / email）
     * @return 注册成功的用户信息（不含密码）
     */
    User register(RegisterRequest req);

    /**
     * 用户登录
     *
     * @param req 登录请求（含 username / password）
     * @return JWT Token 字符串
     */
    String login(LoginRequest req);

    /**
     * 用户登出
     *
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 获取当前登录用户信息（从 JWT + Redis 中获取）
     *
     * @param token JWT Token
     * @return 包含 userId、username、loginTime 等信息
     */
    java.util.Map<String, Object> getCurrentUser(String token);

    /**
     * 注销账号（用户自行删除）
     *
     * @param token JWT Token
     */
    void deleteAccount(String token);
}
