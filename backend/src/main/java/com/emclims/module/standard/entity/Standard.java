package com.emclims.module.standard.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 标准主表实体
 * 对应数据库 standard 表
 */
@TableName("standard")
@Data
@EqualsAndHashCode(callSuper = true)
public class Standard extends BaseEntity {

    /** 标准编号 */
    private String code;

    /** 标准名称 */
    private String name;

    /** 版本号 */
    private String version;

    /** 发布机构 */
    private String issuingOrg;

    /** 生效日期 */
    private LocalDate effectiveDate;

    /** 失效日期 */
    private LocalDate expiryDate;

    /** 状态（0-禁用，1-启用） */
    private String status;

    /** 标准类型（emission-发射，immunity-抗扰度） */
    private String type;
}
