package com.emclims.module.equipment.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 设备查询 DTO
 */
@Data
public class EquipmentQueryDTO {

    /** 搜索关键字（设备编号/名称） */
    private String keyword;

    /** 状态：normal-正常，maintenance-维护中，calibration-校准中，scrap-报废 */
    private String status;

    /** 存放位置 */
    private String location;

    /** 下次校准日期-开始 */
    private LocalDate calibrationDueStart;

    /** 下次校准日期-结束 */
    private LocalDate calibrationDueEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
