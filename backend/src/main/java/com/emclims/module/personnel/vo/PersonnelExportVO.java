package com.emclims.module.personnel.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员档案导出视图对象
 */
@Data
public class PersonnelExportVO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("学历")
    private String education;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("职称")
    private String title;

    @ExcelProperty("入职日期")
    private LocalDate hireDate;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
