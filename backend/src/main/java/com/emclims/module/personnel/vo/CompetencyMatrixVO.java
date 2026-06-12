package com.emclims.module.personnel.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能力矩阵视图对象
 */
@Data
public class CompetencyMatrixVO {

    private Long id;

    /** 人员ID */
    private Long personnelId;

    /** 人员姓名 */
    private String personnelName;

    /** 测试项目类型 */
    private String testItemType;

    /** 考核日期 */
    private LocalDate assessmentDate;

    /** 得分 */
    private Double score;

    /** 考核人ID */
    private Long assessorId;

    /** 考核人姓名 */
    private String assessorName;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
