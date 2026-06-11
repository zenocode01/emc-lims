package com.emclims.module.customer.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户导出视图对象
 */
@Data
public class CustomerExportVO {

    @ExcelProperty("客户名称")
    private String name;

    @ExcelProperty("客户类型")
    private String typeName;

    @ExcelProperty("行业")
    private String industry;

    @ExcelProperty("联系人")
    private String contact;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
