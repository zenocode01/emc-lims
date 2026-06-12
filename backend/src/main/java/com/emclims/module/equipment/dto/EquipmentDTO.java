package com.emclims.module.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备新增/编辑 DTO
 */
@Data
public class EquipmentDTO {

    /** 设备ID（编辑时必填） */
    private Long id;

    /** 设备名称 */
    @NotBlank(message = "设备名称不能为空")
    private String name;

    /** 型号 */
    private String model;

    /** 生产厂家 */
    private String manufacturer;

    /** 序列号 */
    private String serialNo;

    /** 存放位置 */
    private String location;

    /** 状态：normal-正常，maintenance-维护中，calibration-校准中，scrap-报废 */
    private String status;

    /** 下次校准日期 */
    private LocalDate calibrationDue;

    /** 上次校准日期 */
    private LocalDate lastCalibration;

    /** 备注 */
    private String remark;
}
