package com.emclims.module.sys.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户导出视图对象
 */
@Data
public class SysUserExportVO {

    @ExcelProperty("登录账号")
    private String username;

    @ExcelProperty("姓名")
    private String nickname;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("性别")
    private String sexName;

    @ExcelProperty("部门名称")
    private String deptName;

    @ExcelProperty("角色名称")
    private String roleName;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("职位")
    private String post;

    @ExcelProperty("工号")
    private String employeeCode;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
