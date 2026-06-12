package com.emclims.module.report.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 报告查询 DTO
 */
@Data
public class ReportQueryDTO {

    /** 搜索关键字（报告编号/样品编号） */
    private String keyword;

    /** 客户ID */
    private Long customerId;

    /** 报告状态 */
    private String status;

    /** 创建时间-开始 */
    private LocalDate createTimeStart;

    /** 创建时间-结束 */
    private LocalDate createTimeEnd;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;
}
