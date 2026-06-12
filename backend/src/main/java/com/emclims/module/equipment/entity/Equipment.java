package com.emclims.module.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备台账实体
 * 对应数据库 equipment 表
 */
@TableName("equipment")
@Data
@EqualsAndHashCode(callSuper = true)
public class Equipment extends BaseEntity {

    /** 设备编号（唯一） */
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

    /** 状态：normal-正常，maintenance-维护中，calibration-校准中，scrap-报废 */
    private String status;

    /** 下次校准日期 */
    private LocalDate calibrationDue;

    /** 上次校准日期 */
    private LocalDate lastCalibration;
}
