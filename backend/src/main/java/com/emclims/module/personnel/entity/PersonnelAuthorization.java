package com.emclims.module.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 授权上岗记录实体
 * 对应数据库 personnel_authorization 表
 */
@TableName("personnel_authorization")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonnelAuthorization extends BaseEntity {

    /** 人员ID */
    private Long personnelId;

    /** 授权日期 */
    private LocalDate authorizationDate;

    /** 有效期至 */
    private LocalDate expireDate;

    /** 授权人ID（关联 sys_user 表） */
    private Long authorizerId;

    /** 授权项目 */
    private String authorizationItem;
}
