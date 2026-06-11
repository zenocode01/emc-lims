package com.emclims.module.sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 样品编辑 DTO
 */
@Data
public class SampleDTO {

    private Long id;

    /** 客户ID */
    @NotNull(message = "客户不能为空")
    private Long customerId;

    /** 合同ID */
    private Long contractId;

    /** 产品名称 */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /** 型号 */
    private String model;

    /** 生产厂家 */
    private String manufacturer;

    /** 批号/序列号 */
    private String batchNo;

    /** 收样日期 */
    @NotNull(message = "收样日期不能为空")
    private LocalDate receiveDate;

    /** 样品数量 */
    private Integer sampleCount;

    /** 测试标准 */
    private String testStandards;

    /** 测试要求 */
    private String testRequirements;

    /** 测试工程师ID */
    private Long testerId;

    /** 备注 */
    private String remark;
}
