package com.emclims.module.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 人员档案实体
 * 对应数据库 personnel 表
 */
@TableName("personnel")
@Data
@EqualsAndHashCode(callSuper = true)
public class Personnel extends BaseEntity {

    /** 用户ID（关联 sys_user 表） */
    private Long userId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCard;

    /** 学历 */
    private String education;

    /** 专业 */
    private String major;

    /** 职称 */
    private String title;

    /** 入职日期 */
    private LocalDate hireDate;

    /** 状态（0-停用，1-启用） */
    private String status;
}
