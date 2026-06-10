package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 * 对应角色表 sys_role
 */
@TableName("sys_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 角色描述 */
    private String roleDesc;

    /** 数据权限范围（1-全部，2-本部门，3-本部门及子部门，4-仅本人） */
    private Integer dataScope;

    /** 状态（0-禁用，1-启用） */
    private Integer status;

    /** 排序 */
    private Integer sort;
}
