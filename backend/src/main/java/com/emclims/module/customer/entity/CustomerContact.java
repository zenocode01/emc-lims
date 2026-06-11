package com.emclims.module.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联系人实体
 * 对应数据库 customer_contact 表
 */
@TableName("customer_contact")
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerContact extends BaseEntity {

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
}
