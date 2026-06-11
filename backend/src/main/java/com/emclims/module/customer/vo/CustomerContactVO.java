package com.emclims.module.customer.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联系人视图对象
 */
@Data
public class CustomerContactVO {

    private Long id;

    /** 客户ID */
    private Long customerId;

    /** 联系人姓名 */
    private String name;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 职位 */
    private String position;

    /** 是否主要联系人：0-否，1-是 */
    private Integer isPrimary;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
}
