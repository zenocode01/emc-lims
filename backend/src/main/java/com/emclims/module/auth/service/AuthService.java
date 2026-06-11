package com.emclims.module.auth.service;

import com.emclims.module.sys.dto.LoginRequest;
import com.emclims.module.sys.dto.LoginResponse;

/**
 * 认证 Service
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     */
    LoginResponse getCurrentUserInfo();

    /**
     * 刷新 Token
     */
    LoginResponse refreshToken(String token);

    /**
     * 重置密码（需验证旧密码）
     *
     * @param userId       用户ID
     * @param oldPassword  旧密码
     * @param newPassword  新密码
     */
    void resetPassword(Long userId, String oldPassword, String newPassword);
}
