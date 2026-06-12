package com.emclims.module.personnel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 人员档案编辑 DTO
 */
@Data
public class PersonnelDTO {

    private Long id;

    /** 用户ID（关联 sys_user 表） */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 身份证号 */
    private String idCard;

    /** 学历 */
    private String education;

    /** 专业 */
    private String major;

    /** 职称 */
    private String title;

    /** 入职日期 */
    private LocalDate hireDate;

    /** 状态（0-停用，1-启用） */
    private String status;

    /** 备注 */
    private String remark;
}
