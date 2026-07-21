package com.example.demo.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试 — 纯逻辑测试，无需 mock。
 */
class JwtUtilTest {

    // HMAC-SHA256 要求密钥至少 256 bit（32 字节）
    private static final String SECRET = "this-is-a-test-secret-key-that-is-long-enough-32bytes!";
    private static final long ONE_HOUR = 3_600_000L;
    private static final long ONE_MILLISECOND = 1L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, ONE_HOUR);
    }

    @Test
    void generateAndParse_roundTrip_returnsCorrectClaims() {
        String token = jwtUtil.generateToken(42L, "alice", "admin");

        assertNotNull(token, "生成的 token 不应为空");

        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
        assertEquals("alice", jwtUtil.getUsernameFromToken(token));
        assertEquals("admin", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = jwtUtil.generateToken(1L, "test", "student");

        assertTrue(jwtUtil.validateToken(token), "有效 token 应通过验证");
    }

    @Test
    void validateToken_expiredToken_returnsFalse() throws InterruptedException {
        // 构造超短过期时间的 token（1 毫秒）
        JwtUtil shortLived = new JwtUtil(SECRET, ONE_MILLISECOND);
        String token = shortLived.generateToken(1L, "ephemeral", "student");

        // 等 2ms 让它彻底过期
        Thread.sleep(2);

        assertFalse(shortLived.validateToken(token), "过期 token 验证应返回 false");
    }

    @Test
    void validateToken_garbledToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken("not.a.valid.jwt.token"));
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void getRemainingTime_validToken_positive() {
        String token = jwtUtil.generateToken(1L, "test", "student");

        long remaining = jwtUtil.getRemainingTime(token);

        assertTrue(remaining > 0, "有效 token 剩余时间应 > 0");
        assertTrue(remaining <= ONE_HOUR, "剩余时间不应超过有效期");
    }

    @Test
    void getRoleFromToken_studentRole_returnsStudent() {
        String token = jwtUtil.generateToken(1L, "student1", "student");

        assertEquals("student", jwtUtil.getRoleFromToken(token));
    }
}
