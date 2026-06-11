package com.emclims.module.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 联系人编辑 DTO
 */
@Data
public class CustomerContactDTO {

    private Long id;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "联系人姓名不能为空")
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
}
