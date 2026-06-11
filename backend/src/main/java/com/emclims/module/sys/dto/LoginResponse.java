package com.emclims.module.sys.dto;

import lombok.Data;

import java.util.List;

/**
 * 登录响应 DTO
 */
@Data
public class LoginResponse {

    /** JWT Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 权限标识列表 */
    private List<String> permissions;
}
