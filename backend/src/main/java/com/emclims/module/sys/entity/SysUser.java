package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户实体
 * 对应用户表 sys_user
 */
@TableName("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    /** 登录账号 */
    private String username;

    /** 密码 */
    private String password;

    /** 姓名（数据库字段名：name） */
    @TableField("name")
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 性别（数据库字段名：gender, 0-未知，1-男，2-女） */
    @TableField("gender")
    private Integer sex;

    /** 头像URL */
    private String avatar;

    /** 状态（0-禁用，1-启用） */
    private Integer status;

    /** 部门ID */
    private Long deptId;

    /** 主角色ID */
    private Long roleId;

    /** 生日（数据库扩展字段） */
    private LocalDate birthday;

    /** 职位 */
    private String post;

    /** 工号 */
    private String employeeCode;
}
