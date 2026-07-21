package com.example.demo.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.JwtUtil;
import com.example.demo.common.dto.LoginRequest;
import com.example.demo.common.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.auth.mapper.UserMapper;
import com.example.demo.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 * <p>
 * 核心流程：
 * <pre>
 *   注册：用户名查重 → BCrypt 加密 → 存 MySQL → 返回用户信息
 *   登录：查询用户 → BCrypt 验密 → 生成 JWT → 存入 Redis（token:active:{userId}）
 *   登出：校验 Token → 加入 Redis 黑名单（token:blacklist:{token}）→ 删除活跃记录
 * </pre>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.invite-code}")
    private String adminInviteCode;

    @Override
    @Transactional
    public User register(RegisterRequest req) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, req.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new IllegalArgumentException("用户名已存在: " + req.getUsername());
        }

        // 加密密码后保存
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        // 角色判定：邀请码正确 → admin，否则 → student
        String role = "student";
        if (req.getInviteCode() != null && req.getInviteCode().equals(adminInviteCode)) {
            role = "admin";
        }
        user.setRole(role);
        userMapper.insert(user);

        log.info("[Auth] 用户注册成功: username={}, role={}, id={}", req.getUsername(), user.getRole(), user.getId());

        // 清除密码后返回
        user.setPassword(null);
        return user;
    }

    @Override
    public String login(LoginRequest req) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, req.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 生成 JWT（含角色信息）
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // --- Redis 集成：存储活跃 Token ---
        // Key = token:active:{userId}, Value = Token 字符串, TTL = Token 剩余有效期
        String redisKey = "token:active:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, jwtUtil.getRemainingTime(token), TimeUnit.MILLISECONDS);

        log.info("[Auth] 用户登录成功: username={}, userId={}, tokenStoredInRedis=true", req.getUsername(), user.getId());
        return token;
    }

    @Override
    public void logout(String token) {
        if (!jwtUtil.validateToken(token)) {
            log.debug("[Auth] Token 已失效，无需登出处理");
            return;
        }

        // --- Redis 黑名单机制 ---
        // 将 Token 加入 Redis 黑名单，过期时间 = Token 剩余有效期
        Long userId = jwtUtil.getUserIdFromToken(token);
        String blacklistKey = "token:blacklist:" + token;
        long remainingTime = jwtUtil.getRemainingTime(token);

        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(blacklistKey, String.valueOf(userId), remainingTime, TimeUnit.MILLISECONDS);
        }

        // 删除活跃 Token 记录
        String activeKey = "token:active:" + userId;
        redisTemplate.delete(activeKey);

        log.info("[Auth] 用户登出成功: userId={}, tokenBlacklisted=true, activeTokenRemoved=true", userId);
    }

    @Override
    public java.util.Map<String, Object> getCurrentUser(String token) {
        // 校验 Token 有效性
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token 无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        // 检查 Token 是否在黑名单中（已登出）
        String blacklistKey = "token:blacklist:" + token;
        if (redisTemplate.hasKey(blacklistKey)) {
            throw new IllegalArgumentException("Token 已失效，请重新登录");
        }

        // 检查 Redis 中是否有活跃 Token 记录
        String activeKey = "token:active:" + userId;
        String activeToken = redisTemplate.opsForValue().get(activeKey);
        boolean isActiveInRedis = activeToken != null && activeToken.equals(token);

        // 构建返回信息
        java.util.Map<String, Object> info = new java.util.LinkedHashMap<>();
        info.put("userId", userId);
        info.put("username", username);
        info.put("role", role);
        info.put("isActiveInRedis", isActiveInRedis);
        info.put("tokenRemainingMs", jwtUtil.getRemainingTime(token));

        return info;
    }

    @Override
    @Transactional
    public void deleteAccount(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token 无效或已过期");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userMapper.deleteById(userId);

        // 清理 Redis 中的 token 记录
        String activeKey = "token:active:" + userId;
        redisTemplate.delete(activeKey);
        String blacklistKey = "token:blacklist:" + token;
        redisTemplate.delete(blacklistKey);

        log.info("[Auth] 用户已注销账号: userId={}", userId);
    }
}
