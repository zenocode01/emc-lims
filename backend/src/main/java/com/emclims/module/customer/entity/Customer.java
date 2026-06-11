package com.emclims.module.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户实体
 * 对应数据库 customer 表
 */
@TableName("customer")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    /** 客户名称 */
    private String name;

    /** 类型：1-企业，2-个人 */
    private Integer type;

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
}
