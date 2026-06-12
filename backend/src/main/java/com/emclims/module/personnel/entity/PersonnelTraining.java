package com.emclims.module.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 培训记录实体
 * 对应数据库 personnel_training 表
 */
@TableName("personnel_training")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonnelTraining extends BaseEntity {

    /** 人员ID */
    private Long personnelId;

    /** 培训课程 */
    private String course;

    /** 培训师 */
    private String trainer;

    /** 培训日期 */
    private LocalDate trainDate;

    /** 培训时长（小时） */
    private Integer duration;

    /** 结果（0-不合格，1-合格） */
    private String result;

    /** 证书编号 */
    private String certificate;
}
