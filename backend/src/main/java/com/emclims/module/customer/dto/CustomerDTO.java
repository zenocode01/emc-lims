package com.emclims.module.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 客户编辑 DTO
 */
@Data
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "客户名称不能为空")
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

    /** 备注 */
    private String remark;
}
