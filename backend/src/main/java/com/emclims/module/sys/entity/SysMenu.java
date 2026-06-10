package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单/权限实体
 * 对应菜单表 sys_menu
 */
@TableName("sys_menu")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型（1-目录，2-菜单，3-按钮） */
    private String menuType;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识 */
    private String permission;

    /** 父菜单ID（0表示顶级菜单） */
    private Long parentId;

    /** 排序 */
    private Integer sort;

    /** 图标 */
    private String icon;

    /** 是否隐藏（0-显示，1-隐藏） */
    private Integer isHidden;

    /** 重定向地址 */
    private String redirect;

    /** 状态（0-禁用，1-启用） */
    private Integer status;

    /** 创建人 */
    private Long createBy;

    /** 更新人 */
    private Long updateBy;

    /** 部门ID */
    private Long deptId;
}
