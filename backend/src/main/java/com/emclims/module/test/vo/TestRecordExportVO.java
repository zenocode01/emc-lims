package com.emclims.module.test.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试记录导出视图对象
 */
@Data
public class TestRecordExportVO {

    @ExcelProperty("计划编号")
    private String planNo;

    @ExcelProperty("样品编号")
    private String sampleNo;

    @ExcelProperty("测试项目代码")
    private String testItemCode;

    @ExcelProperty("测试项目名称")
    private String testItemName;

    @ExcelProperty("测试人员名称")
    private String testerName;

    @ExcelProperty("测试日期")
    private LocalDateTime testDate;

    @ExcelProperty("结果名称")
    private String resultName;

    @ExcelProperty("测量值")
    private String measurementValue;

    @ExcelProperty("限值")
    private String limitValue;

    @ExcelProperty("余量")
    private String margin;

    @ExcelProperty("测试条件")
    private String testCondition;

    @ExcelProperty("备注")
    private String remarks;
}
