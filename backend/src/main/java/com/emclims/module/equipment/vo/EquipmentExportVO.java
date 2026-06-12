package com.emclims.module.equipment.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备导出视图对象
 */
@Data
public class EquipmentExportVO {

    @ExcelProperty("设备编号")
    private String equipmentNo;

    @ExcelProperty("设备名称")
    private String name;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("生产厂家")
    private String manufacturer;

    @ExcelProperty("序列号")
    private String serialNo;

    @ExcelProperty("存放位置")
    private String location;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("下次校准日期")
    private LocalDate calibrationDue;

    @ExcelProperty("上次校准日期")
    private LocalDate lastCalibration;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
