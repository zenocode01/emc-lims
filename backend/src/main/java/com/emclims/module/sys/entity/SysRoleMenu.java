package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色菜单关联实体
 * 对应角色菜单关联表 sys_role_menu
 */
@TableName("sys_role_menu")
@Data
public class SysRoleMenu {

    @TableId
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}
