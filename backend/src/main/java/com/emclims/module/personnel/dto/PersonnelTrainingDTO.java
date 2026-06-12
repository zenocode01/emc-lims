package com.emclims.module.personnel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 培训记录编辑 DTO
 */
@Data
public class PersonnelTrainingDTO {

    private Long id;

    /** 人员ID */
    @NotNull(message = "人员ID不能为空")
    private Long personnelId;

    /** 培训课程 */
    @NotBlank(message = "培训课程不能为空")
    private String course;

    /** 培训师 */
    private String trainer;

    /** 培训日期 */
    @NotNull(message = "培训日期不能为空")
    private LocalDate trainDate;

    /** 培训时长（小时） */
    private Integer duration;

    /** 结果（0-不合格，1-合格） */
    private String result;

    /** 证书编号 */
    private String certificate;

    /** 备注 */
    private String remark;
}
