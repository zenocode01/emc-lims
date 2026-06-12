package com.emclims.module.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门导出视图对象
 */
@Data
public class SysDeptExportVO {

    @ExcelProperty("部门名称")
    private String deptName;

    @ExcelProperty("部门编码")
    private String deptCode;

    @ExcelProperty("部门类型")
    private String deptTypeName;

    @ExcelProperty("父部门")
    private String parentName;

    @ExcelProperty("负责人")
    private Long leader;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("排序")
    private Integer sort;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
