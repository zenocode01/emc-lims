package com.emclims.module.personnel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 能力矩阵查询 DTO
 */
@Data
public class CompetencyMatrixQueryDTO {

    /** 人员ID */
    private Long personnelId;

    /** 测试项目类型 */
    private String testItemType;

    /** 考核日期-开始 */
    private LocalDate assessmentDateStart;

    /** 考核日期-结束 */
    private LocalDate assessmentDateEnd;

    /** 最低得分 */
    private Double minScore;

    /** 最高得分 */
    private Double maxScore;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
