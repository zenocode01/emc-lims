package com.emclims.module.report.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报告导出视图对象
 */
@Data
public class ReportExportVO {

    @ExcelProperty("报告编号")
    private String reportNo;

    @ExcelProperty("样品编号")
    private String sampleNo;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("版本号")
    private Integer version;

    @ExcelProperty("审核人名称")
    private String reviewerName;

    @ExcelProperty("批准人名称")
    private String approverName;

    @ExcelProperty("签发日期")
    private LocalDate issuedDate;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
