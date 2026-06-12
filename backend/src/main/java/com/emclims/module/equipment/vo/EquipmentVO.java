package com.emclims.module.equipment.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备视图对象
 */
@Data
public class EquipmentVO {

    /** 设备ID */
    private Long id;

    /** 设备编号 */
    private String equipmentNo;

    /** 设备名称 */
    private String name;

    /** 型号 */
    private String model;

    /** 生产厂家 */
    private String manufacturer;

    /** 序列号 */
    private String serialNo;

    /** 存放位置 */
    private String location;

    /** 状态码 */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 下次校准日期 */
    private LocalDate calibrationDue;

    /** 上次校准日期 */
    private LocalDate lastCalibration;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
