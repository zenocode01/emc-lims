package com.emclims.module.personnel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 培训记录查询 DTO
 */
@Data
public class PersonnelTrainingQueryDTO {

    /** 人员ID */
    private Long personnelId;

    /** 培训课程 */
    private String course;

    /** 培训师 */
    private String trainer;

    /** 培训日期-开始 */
    private LocalDate trainDateStart;

    /** 培训日期-结束 */
    private LocalDate trainDateEnd;

    /** 结果 */
    private String result;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
