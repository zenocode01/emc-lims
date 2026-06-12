package com.emclims.module.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 能力矩阵实体
 * 对应数据库 competency_matrix 表
 */
@TableName("competency_matrix")
@Data
@EqualsAndHashCode(callSuper = true)
public class CompetencyMatrix extends BaseEntity {

    /** 人员ID */
    private Long personnelId;

    /** 测试项目类型 */
    private String testItemType;

    /** 考核日期 */
    private LocalDate assessmentDate;

    /** 得分 */
    private Double score;

    /** 考核人ID（关联 sys_user 表） */
    private Long assessorId;
}
