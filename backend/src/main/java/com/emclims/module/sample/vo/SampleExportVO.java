package com.emclims.module.sample.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 样品导出视图对象
 */
@Data
public class SampleExportVO {

    @ExcelProperty("样品编号")
    private String sampleNo;

    @ExcelProperty("产品名称")
    private String productName;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("生产厂家")
    private String manufacturer;

    @ExcelProperty("批号")
    private String batchNo;

    @ExcelProperty("收样日期")
    private LocalDate receiveDate;

    @ExcelProperty("样品数量")
    private Integer sampleCount;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("测试标准")
    private String testStandards;

    @ExcelProperty("测试要求")
    private String testRequirements;

    @ExcelProperty("测试工程师")
    private String testerName;

    @ExcelProperty("收样人")
    private String receiveByName;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
