package com.emclims.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.security.JwtUtils;
import com.emclims.module.sys.dto.LoginRequest;
import com.emclims.module.sys.dto.LoginResponse;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysMenuMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.auth.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证 Service 实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserMapper userMapper, SysMenuMapper menuMapper,
                           JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.menuMapper = menuMapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录，用户名: {}", request.getUsername());
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码（BCrypt）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 验证状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已禁用");
        }

        // 查询用户权限
        List<String> permissions = menuMapper.selectPermissionsByUserId(user.getId());

        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    @Override
    public void logout() {
        log.info("用户登出");
        // TODO: 将 Token 加入黑名单（Redis）
        // 简化处理：前端删除 Token 即可
    }

    @Override
    public LoginResponse getCurrentUserInfo() {
        // 从 SecurityContext 获取当前用户
        log.debug("获取当前用户信息");
        Long userId = getCurrentUserId();
        SysUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户权限
        List<String> permissions = menuMapper.selectPermissionsByUserId(userId);

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    @Override
    public LoginResponse refreshToken(String token) {
        log.info("刷新 Token");
        // 验证旧 Token
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException("Token 无效");
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        SysUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> permissions = menuMapper.selectPermissionsByUserId(userId);
        String newToken = jwtUtils.generateToken(userId, user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    @Override
    public void resetPassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private Long getCurrentUserId() {
        return com.emclims.common.security.SecurityUtils.getCurrentUserId();
    }
}
