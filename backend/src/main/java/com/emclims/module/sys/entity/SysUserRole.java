package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体
 * 对应用户角色关联表 sys_user_role
 */
@TableName("sys_user_role")
@Data
public class SysUserRole {

    @TableId
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;
}
