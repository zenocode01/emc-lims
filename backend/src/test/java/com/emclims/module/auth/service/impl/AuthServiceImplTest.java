package com.emclims.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.security.JwtUtils;
import com.emclims.module.sys.dto.LoginRequest;
import com.emclims.module.sys.dto.LoginResponse;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysMenuMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysMenuMapper menuMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userMapper, menuMapper, jwtUtils, passwordEncoder);
    }

    @Test
    void testLoginSuccess() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("password123");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setPassword("$2a$10$encodedPassword");
        user.setNickname("测试用户");
        user.setStatus(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(List.of("sys:user:list", "sys:role:list"));
        when(jwtUtils.generateToken(1L, "13800138000")).thenReturn("test.jwt.token");

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("13800138000", response.getPhone());
        assertEquals("测试用户", response.getNickname());
        assertEquals(2, response.getPermissions().size());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () -> authService.login(request), "用户不存在");
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("wrongPassword");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setPassword("$2a$10$encodedPassword");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(request), "密码错误");
    }

    @Test
    void testLoginUserDisabled() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("password123");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setPassword("$2a$10$encodedPassword");
        user.setStatus(0); // 已禁用

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.login(request), "用户已禁用");
    }

    @Test
    void testRefreshToken() {
        String oldToken = "old.jwt.token";
        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setNickname("测试用户");

        when(jwtUtils.validateToken(oldToken)).thenReturn(true);
        when(jwtUtils.getUserIdFromToken(oldToken)).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(List.of("sys:user:list"));
        when(jwtUtils.generateToken(1L, "13800138000")).thenReturn("new.jwt.token");

        LoginResponse response = authService.refreshToken(oldToken);

        assertNotNull(response);
        assertEquals("new.jwt.token", response.getToken());
    }

    @Test
    void testRefreshTokenInvalid() {
        when(jwtUtils.validateToken("invalid.token")).thenReturn(false);
        assertThrows(BusinessException.class, () -> authService.refreshToken("invalid.token"), "Token 无效");
    }
}
