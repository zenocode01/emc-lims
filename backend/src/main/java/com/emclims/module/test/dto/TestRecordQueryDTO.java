package com.emclims.module.test.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 测试记录查询 DTO
 */
@Data
public class TestRecordQueryDTO {

    /** 测试计划ID */
    private Long testPlanId;

    /** 测试项目ID */
    private Long testItemId;

    /** 测试人员ID */
    private Long testerId;

    /** 结果 */
    private String result;

    /** 开始日期 */
    private LocalDateTime startDate;

    /** 结束日期 */
    private LocalDateTime endDate;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
