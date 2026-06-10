package com.emclims.common.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils 单元测试
 */
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();
        // 使用反射设置私有字段
        var secretField = JwtUtils.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, "emc-lims-jwt-secret-key-must-be-at-least-256-bits");
        
        var expirationField = JwtUtils.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtils, 86400000L);
        
        // 手动调用 @PostConstruct init 方法初始化 signingKey
        var initMethod = JwtUtils.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(jwtUtils);
    }

    @Test
    void testGenerateAndParseToken() {
        String token = jwtUtils.generateToken(1L, "13800138000");
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtUtils.parseToken(token);
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
        assertEquals("13800138000", claims.get("username"));
    }

    @Test
    void testValidateValidToken() {
        String token = jwtUtils.generateToken(2L, "13900139000");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void testValidateNullToken() {
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtUtils.generateToken(42L, "user42");
        Long userId = jwtUtils.getUserIdFromToken(token);
        assertEquals(42L, userId);
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtUtils.generateToken(1L, "test_user");
        String username = jwtUtils.getUsernameFromToken(token);
        assertEquals("test_user", username);
    }
}
