package com.emclims.module.standard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 标准编辑 DTO
 */
@Data
@Schema(description = "标准编辑DTO")
public class StandardDTO {

    /** ID（更新时必填） */
    @Schema(description = "ID，更新时必填")
    private Long id;

    /** 标准编号 */
    @NotBlank(message = "标准编号不能为空")
    @Schema(description = "标准编号", example = "GB/T 17626.2")
    private String code;

    /** 标准名称 */
    @NotBlank(message = "标准名称不能为空")
    @Schema(description = "标准名称", example = "静电放电抗扰度试验")
    private String name;

    /** 版本号 */
    @Schema(description = "版本号", example = "2006")
    private String version;

    /** 发布机构 */
    @Schema(description = "发布机构", example = "国家标准化管理委员会")
    private String issuingOrg;

    /** 生效日期 */
    @Schema(description = "生效日期", example = "2006-10-01")
    private LocalDate effectiveDate;

    /** 失效日期 */
    @Schema(description = "失效日期", example = "2026-10-01")
    private LocalDate expiryDate;

    /** 状态（0-禁用，1-启用） */
    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private String status;

    /** 标准类型（emission-发射，immunity-抗扰度） */
    @NotBlank(message = "标准类型不能为空")
    @Schema(description = "标准类型（emission-发射，immunity-抗扰度）", example = "immunity")
    private String type;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
