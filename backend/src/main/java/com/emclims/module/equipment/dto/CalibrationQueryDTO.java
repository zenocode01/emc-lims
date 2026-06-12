package com.emclims.module.equipment.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 校准记录查询 DTO
 */
@Data
public class CalibrationQueryDTO {

    /** 设备ID */
    private Long equipmentId;

    /** 校准日期-开始 */
    private LocalDate calibrationDateStart;

    /** 校准日期-结束 */
    private LocalDate calibrationDateEnd;

    /** 校准结果 */
    private String result;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
