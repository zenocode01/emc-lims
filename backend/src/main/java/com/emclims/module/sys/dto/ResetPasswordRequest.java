package com.emclims.module.sys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 密码重置请求
 */
@Data
public class ResetPasswordRequest {

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
