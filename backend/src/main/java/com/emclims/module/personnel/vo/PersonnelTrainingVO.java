package com.emclims.module.personnel.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 培训记录视图对象
 */
@Data
public class PersonnelTrainingVO {

    private Long id;

    /** 人员ID */
    private Long personnelId;

    /** 人员姓名 */
    private String personnelName;

    /** 培训课程 */
    private String course;

    /** 培训师 */
    private String trainer;

    /** 培训日期 */
    private LocalDate trainDate;

    /** 培训时长（小时） */
    private Integer duration;

    /** 结果码 */
    private String result;

    /** 结果名称 */
    private String resultName;

    /** 证书编号 */
    private String certificate;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
