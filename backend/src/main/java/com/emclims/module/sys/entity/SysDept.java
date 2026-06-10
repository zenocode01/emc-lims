package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 * 对应数据库 sys_dept 表
 */
@TableName("sys_dept")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDept extends BaseEntity {

    /** 部门名称（数据库字段名：name） */
    @TableField("name")
    private String deptName;

    /** 部门编码（数据库字段名：code） */
    @TableField("code")
    private String deptCode;

    /** 部门类型（1-公司，2-部门，3-小组） */
    private Integer deptType;

    /** 父部门ID（0表示顶级部门） */
    private Long parentId;

    /** 负责人 */
    private Long leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 排序 */
    private Integer sort;

    /** 状态（0-禁用，1-启用） */
    private Integer status;
}
