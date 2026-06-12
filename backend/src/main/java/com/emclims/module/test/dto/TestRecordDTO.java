package com.emclims.module.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试记录 DTO
 */
@Data
public class TestRecordDTO {

    private Long id;

    @NotNull(message = "测试计划ID不能为空")
    private Long testPlanId;

    @NotNull(message = "测试项目ID不能为空")
    private Long testItemId;

    @NotNull(message = "测试人员ID不能为空")
    private Long testerId;

    @NotNull(message = "测试日期不能为空")
    private LocalDateTime testDate;

    @NotBlank(message = "测试结果不能为空")
    private String result;

    private String measurementValue;

    private String limitValue;

    private BigDecimal margin;

    private Long instrumentId;

    private String testCondition;

    private String environment;

    private String remarks;
}
