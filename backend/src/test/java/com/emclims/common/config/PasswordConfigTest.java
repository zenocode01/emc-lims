package com.emclims.common.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordConfig 配置类单元测试
 */
class PasswordConfigTest {

    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new PasswordConfig().passwordEncoder();
    }

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(encoder);
        assertInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void testPasswordEncodeAndMatch() {
        String rawPassword = "test-password-123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncodeConsistency() {
        String rawPassword = "same-password";
        String encoded1 = encoder.encode(rawPassword);
        String encoded2 = encoder.encode(rawPassword);

        // BCrypt encodes with random salt, so encoded values differ
        assertNotEquals(encoded1, encoded2);
        assertTrue(encoder.matches(rawPassword, encoded1));
        assertTrue(encoder.matches(rawPassword, encoded2));
    }

    @Test
    void testWrongPassword() {
        String rawPassword = "correct-password";
        String encoded = encoder.encode(rawPassword);

        assertFalse(encoder.matches("wrong-password", encoded));
    }

    @Test
    void testEmptyPassword() {
        String empty = "";
        String encoded = encoder.encode(empty);

        assertNotNull(encoded);
        assertNotEquals(empty, encoded);
        assertTrue(encoder.matches(empty, encoded));
    }

    @Test
    void testLongPassword() {
        String longPassword = "a".repeat(1000);
        String encoded = encoder.encode(longPassword);

        assertNotNull(encoded);
        assertTrue(encoder.matches(longPassword, encoded));
    }
}
