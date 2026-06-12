package com.emclims.module.personnel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 能力矩阵编辑 DTO
 */
@Data
public class CompetencyMatrixDTO {

    private Long id;

    /** 人员ID */
    @NotNull(message = "人员ID不能为空")
    private Long personnelId;

    /** 测试项目类型 */
    @NotBlank(message = "测试项目类型不能为空")
    private String testItemType;

    /** 考核日期 */
    @NotNull(message = "考核日期不能为空")
    private LocalDate assessmentDate;

    /** 得分 */
    @NotNull(message = "得分不能为空")
    private Double score;

    /** 考核人ID */
    @NotNull(message = "考核人不能为空")
    private Long assessorId;

    /** 备注 */
    private String remark;
}
