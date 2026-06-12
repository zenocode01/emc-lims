package com.emclims.module.test.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 测试计划 DTO
 */
@Data
public class TestPlanDTO {

    private Long id;

    @NotNull(message = "样品ID不能为空")
    private Long sampleId;

    private Long customerId;

    /** 测试项目ID列表 */
    private List<Long> testItemIds;

    /** 测试项目配置JSON */
    private String testItems;

    private LocalDate planDate;

    private LocalDate dueDate;

    private String remark;
}
