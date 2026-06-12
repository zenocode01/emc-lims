package com.emclims.module.test.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 测试计划导出视图对象
 */
@Data
public class TestPlanExportVO {

    @ExcelProperty("计划编号")
    private String planNo;

    @ExcelProperty("样品编号")
    private String sampleNo;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("测试项目")
    private String testItems;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("计划日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    @ExcelProperty("到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
