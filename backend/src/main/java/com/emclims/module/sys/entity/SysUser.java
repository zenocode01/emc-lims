package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应用户表 sys_user
 */
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    /** 手机号（登录账号） */
    private String phone;

    /** 部门ID */
    private Long deptId;

    /** 角色ID */
    private Long roleId;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 性别（0-未知，1-男，2-女） */
    private Integer sex;

    /** 头像URL */
    private String avatar;

    /** 状态（0-禁用，1-启用） */
    private Integer status;

    /** 生日 */
    private LocalDate birthday;

    /** 职位 */
    private String post;

    /** 工号 */
    private String employeeCode;
}
