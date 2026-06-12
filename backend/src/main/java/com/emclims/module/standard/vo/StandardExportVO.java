package com.emclims.module.standard.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 标准导出视图对象
 */
@Data
public class StandardExportVO {

    @ExcelProperty("标准编号")
    private String code;

    @ExcelProperty("标准名称")
    private String name;

    @ExcelProperty("版本号")
    private String version;

    @ExcelProperty("发布机构")
    private String issuingOrg;

    @ExcelProperty("标准类型")
    private String typeName;

    @ExcelProperty("生效日期")
    private LocalDate effectiveDate;

    @ExcelProperty("失效日期")
    private LocalDate expiryDate;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
