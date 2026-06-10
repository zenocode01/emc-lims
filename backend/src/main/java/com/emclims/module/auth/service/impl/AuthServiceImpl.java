package com.emclims.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
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
        String token = jwtUtils.generateToken(user.getId(), user.getPhone());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    @Override
    public void logout() {
        // TODO: 将 Token 加入黑名单（Redis）
        // 简化处理：前端删除 Token 即可
    }

    @Override
    public LoginResponse getCurrentUserInfo() {
        // 从 SecurityContext 获取当前用户
        Long userId = getCurrentUserId();
        SysUser user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户权限
        List<String> permissions = menuMapper.selectPermissionsByUserId(userId);

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    @Override
    public LoginResponse refreshToken(String token) {
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
        String newToken = jwtUtils.generateToken(userId, user.getPhone());

        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);

        return response;
    }

    private Long getCurrentUserId() {
        // TODO: 从 SecurityContext 获取当前用户ID
        // 这里简化处理，从请求头或参数获取
        return 1L;
    }
}
