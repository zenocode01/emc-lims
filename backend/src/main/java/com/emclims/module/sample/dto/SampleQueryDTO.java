package com.emclims.module.sample.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 样品查询 DTO
 */
@Data
public class SampleQueryDTO {

    /** 搜索关键字（样品编号/产品名称） */
    private String keyword;

    /** 客户ID */
    private Long customerId;

    /** 状态 */
    private String status;

    /** 收样日期-开始 */
    private LocalDate receiveDateStart;

    /** 收样日期-结束 */
    private LocalDate receiveDateEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
