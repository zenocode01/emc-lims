package com.emclims.module.sample.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 样品视图对象
 */
@Data
public class SampleVO {

    private Long id;

    /** 样品编号 */
    private String sampleNo;

    /** 客户ID */
    private Long customerId;

    /** 客户名称 */
    private String customerName;

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

    /** 状态码 */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 测试标准 */
    private String testStandards;

    /** 测试要求 */
    private String testRequirements;

    /** 测试工程师ID */
    private Long testerId;

    /** 测试工程师 */
    private String testerName;

    /** 收样人ID */
    private Long receiveBy;

    /** 收样人 */
    private String receiveByName;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
}
