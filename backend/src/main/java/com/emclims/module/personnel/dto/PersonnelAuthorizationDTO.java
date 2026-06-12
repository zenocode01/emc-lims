package com.emclims.module.personnel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 授权上岗记录编辑 DTO
 */
@Data
public class PersonnelAuthorizationDTO {

    private Long id;

    /** 人员ID */
    @NotNull(message = "人员ID不能为空")
    private Long personnelId;

    /** 授权日期 */
    @NotNull(message = "授权日期不能为空")
    private LocalDate authorizationDate;

    /** 有效期至 */
    @NotNull(message = "有效期至不能为空")
    private LocalDate expireDate;

    /** 授权人ID */
    @NotNull(message = "授权人不能为空")
    private Long authorizerId;

    /** 授权项目 */
    @NotBlank(message = "授权项目不能为空")
    private String authorizationItem;

    /** 备注 */
    private String remark;
}
