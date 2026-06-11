package com.emclims.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordConfig 配置类单元测试
 */
class PasswordConfigTest {

    @Test
    void testPasswordEncoderBean() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void testPasswordEncodeAndMatch() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "test-password-123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);

        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncodeConsistency() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "same-password";
        String encoded1 = encoder.encode(rawPassword);
        String encoded2 = encoder.encode(rawPassword);

        // BCrypt encodes with random salt, so encoded values differ
        assertNotEquals(encoded1, encoded2);

        // But both should match the original password
        assertTrue(encoder.matches(rawPassword, encoded1));
        assertTrue(encoder.matches(rawPassword, encoded2));
    }

    @Test
    void testWrongPassword() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "correct-password";
        String encoded = encoder.encode(rawPassword);

        assertFalse(encoder.matches("wrong-password", encoded));
    }
}
