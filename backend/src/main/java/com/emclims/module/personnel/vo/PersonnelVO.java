package com.emclims.module.personnel.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员档案视图对象
 */
@Data
public class PersonnelVO {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名称 */
    private String userName;

    /** 姓名 */
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

    /** 状态码 */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
