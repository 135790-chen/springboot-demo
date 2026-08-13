package com.example.demo.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.auth.mapper.UserMapper;
import com.example.demo.common.JwtUtil;
import com.example.demo.common.dto.LoginRequest;
import com.example.demo.common.dto.RegisterRequest;
import com.example.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试
 * <p>
 * 使用真实 JwtUtil、BCryptPasswordEncoder；仅 mock 接口（UserMapper）。
 * Redis/StringRedisTemplate 在 Java 25 下无法 Mockito mock，因此 login/logout/getCurrentUser/deleteAccount
 * 中涉及 Redis 的逻辑仅在集成测试中覆盖。
 */
class AuthServiceImplTest {

    private AuthServiceImpl service;
    private UserMapper userMapper;
    private JwtUtil jwtUtil;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        userMapper = mock(UserMapper.class);
        jwtUtil = new JwtUtil(
                "this-is-a-test-secret-key-that-is-long-enough-32bytes!",
                3_600_000L  // 1 小时
        );
        passwordEncoder = new BCryptPasswordEncoder();
        service = new AuthServiceImpl();

        setField("userMapper", userMapper);
        setField("jwtUtil", jwtUtil);
        setField("passwordEncoder", passwordEncoder);
        setField("adminInviteCode", "admin888");
        // redisTemplate 保持 null —— Java 25 下无法 Mockito mock StringRedisTemplate
    }

    private void setField(String name, Object value) throws Exception {
        var field = AuthServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }

    // ────────── 注册测试 ──────────

    @Test
    void register_newUser_passwordEncrypted() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        final String[] savedPasswordHolder = new String[1];
        when(userMapper.insert(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            savedPasswordHolder[0] = u.getPassword();
            u.setId(1L);
            return 1;
        });

        RegisterRequest req = buildRegisterReq("alice", "123456");
        User result = service.register(req);

        assertNotNull(result, "注册应返回用户对象");
        assertEquals("alice", result.getUsername());
        assertEquals("student", result.getRole(), "未传邀请码 → 默认学生");
        assertNull(result.getPassword(), "返回给前端的 user 不能含密码");

        String savedPassword = savedPasswordHolder[0];
        assertNotNull(savedPassword, "insert 时密码不应为空");
        assertNotEquals("123456", savedPassword, "存库密码必须是密文");
        assertTrue(savedPassword.startsWith("$2a$"), "密文应以 BCrypt 标识开头");
    }

    @Test
    void register_withCorrectInviteCode_roleIsAdmin() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return 1;
        });

        RegisterRequest req = buildRegisterReq("admin_user", "123456");
        req.setInviteCode("admin888");
        User result = service.register(req);

        assertEquals("admin", result.getRole(), "正确邀请码 → 管理员");
    }

    @Test
    void register_wrongInviteCode_roleStillStudent() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return 1;
        });

        RegisterRequest req = buildRegisterReq("user2", "123456");
        req.setInviteCode("wrong-code");
        User result = service.register(req);

        assertEquals("student", result.getRole(), "错误邀请码 → 仍是学生");
    }

    @Test
    void register_duplicateUsername_throwsException() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("bob");
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        RegisterRequest req = buildRegisterReq("bob", "123456");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.register(req));
        assertTrue(ex.getMessage().contains("用户名已存在"));
        verify(userMapper, never()).insert(any());
    }

    // ────────── 登录测试 ──────────

    @Test
    void login_wrongPassword_throwsException() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("test");
        dbUser.setPassword(passwordEncoder.encode("correct"));
        dbUser.setRole("student");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(dbUser);

        LoginRequest req = buildLoginReq("test", "wrong_password");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.login(req));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void login_userNotFound_throwsException() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        LoginRequest req = buildLoginReq("ghost", "whatever");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.login(req));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void login_correctPassword_passesValidationThenNpeOnRedis() {
        User dbUser = new User();
        dbUser.setId(7L);
        dbUser.setUsername("demo");
        dbUser.setPassword(passwordEncoder.encode("123456"));
        dbUser.setRole("admin");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(dbUser);

        // redisTemplate 为 null，密码校验通过后会 NPE
        // 验证：密码匹配通过 → 走到 redisTemplate 调用处 → 说明 selectOne + passwordEncoder.matches 正确
        try {
            service.login(buildLoginReq("demo", "123456"));
        } catch (NullPointerException e) {
            // 预期：密码校验通过后 redisTemplate.opsForValue() NPE
            assertTrue(e.getMessage() == null || e.getMessage().contains("redisTemplate")
                    || e.getMessage().contains("StringRedisTemplate"),
                    "NPE 应来自缺少 Redis，不是其他逻辑错误");
        }
    }

    // ────────── 工具方法 ──────────

    private RegisterRequest buildRegisterReq(String username, String password) {
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }

    private LoginRequest buildLoginReq(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }
}