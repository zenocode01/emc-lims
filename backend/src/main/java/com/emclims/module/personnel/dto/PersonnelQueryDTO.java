package com.emclims.module.personnel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 人员档案查询 DTO
 */
@Data
public class PersonnelQueryDTO {

    /** 搜索关键字（姓名/身份证号） */
    private String keyword;

    /** 学历 */
    private String education;

    /** 职称 */
    private String title;

    /** 状态 */
    private String status;

    /** 入职日期-开始 */
    private LocalDate hireDateStart;

    /** 入职日期-结束 */
    private LocalDate hireDateEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
