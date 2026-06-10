package com.emclims.module.sys.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户视图对象
 */
@Data
public class SysUserVO {

    private Long id;

    private String phone;

    private Long deptId;

    private String deptName;

    private Long roleId;

    private String roleName;

    private String roleCode;

    private String nickname;

    private String email;

    private Integer sex;

    private String avatar;

    private Integer status;

    private LocalDate birthday;

    private String post;

    private String employeeCode;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}
