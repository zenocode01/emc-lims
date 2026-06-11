package com.emclims.module.sample.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 样品实体
 * 对应数据库 sample 表
 */
@TableName("sample")
@Data
@EqualsAndHashCode(callSuper = true)
public class Sample extends BaseEntity {

    /** 样品编号 */
    private String sampleNo;

    /** 客户ID */
    private Long customerId;

    /** 合同ID */
    private Long contractId;

    /** 产品名称 */
    private String productName;

    /** 型号 */
    private String model;

    /** 生产厂家 */
    private String manufacturer;

    /** 批号/序列号 */
    private String batchNo;

    /** 收样日期 */
    private LocalDate receiveDate;

    /** 样品数量 */
    private Integer sampleCount;

    /** 状态 */
    private String status;

    /** 测试标准 */
    private String testStandards;

    /** 测试要求 */
    private String testRequirements;

    /** 测试工程师ID */
    private Long testerId;

    /** 收样人ID */
    private Long receiveBy;
}
