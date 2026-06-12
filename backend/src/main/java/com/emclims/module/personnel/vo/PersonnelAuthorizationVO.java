package com.emclims.module.personnel.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 授权上岗记录视图对象
 */
@Data
public class PersonnelAuthorizationVO {

    private Long id;

    /** 人员ID */
    private Long personnelId;

    /** 人员姓名 */
    private String personnelName;

    /** 授权日期 */
    private LocalDate authorizationDate;

    /** 有效期至 */
    private LocalDate expireDate;

    /** 授权人ID */
    private Long authorizerId;

    /** 授权人姓名 */
    private String authorizerName;

    /** 授权项目 */
    private String authorizationItem;

    /** 状态（0-已过期，1-有效，2-即将过期） */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
