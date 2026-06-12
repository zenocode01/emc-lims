package com.emclims.module.personnel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 授权上岗记录查询 DTO
 */
@Data
public class PersonnelAuthorizationQueryDTO {

    /** 人员ID */
    private Long personnelId;

    /** 授权项目 */
    private String authorizationItem;

    /** 授权日期-开始 */
    private LocalDate authorizationDateStart;

    /** 授权日期-结束 */
    private LocalDate authorizationDateEnd;

    /** 有效期-开始 */
    private LocalDate expireDateStart;

    /** 有效期-结束 */
    private LocalDate expireDateEnd;

    /** 状态（0-已过期，1-有效，2-即将过期） */
    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
