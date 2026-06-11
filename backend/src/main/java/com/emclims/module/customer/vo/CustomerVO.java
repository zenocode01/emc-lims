package com.emclims.module.customer.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户视图对象
 */
@Data
public class CustomerVO {

    private Long id;

    /** 客户名称 */
    private String name;

    /** 类型：1-企业，2-个人 */
    private Integer type;

    /** 类型名称 */
    private String typeName;

    /** 行业 */
    private String industry;

    /** 地址 */
    private String address;

    /** 电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 联系人 */
    private String contact;

    /** 状态（0-禁用，1-启用） */
    private Integer status;

    /** 备注 */
    private String remark;

    private Long createBy;

    private LocalDateTime createTime;

    private Long updateBy;

    private LocalDateTime updateTime;
}
