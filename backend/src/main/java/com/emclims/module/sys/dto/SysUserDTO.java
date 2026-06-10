package com.emclims.module.sys.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户编辑 DTO
 */
@Data
public class SysUserDTO {

    private Long id;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private Long deptId;

    private Long roleId;

    private String password;

    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Integer sex;

    private String avatar;

    private Integer status;

    private LocalDate birthday;

    private String post;

    private String employeeCode;

    /** 角色ID列表（支持多角色） */
    private List<Long> roleIds;
}
