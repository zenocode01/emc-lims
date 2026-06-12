package com.emclims.module.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报告模板新增/编辑 DTO
 */
@Data
public class ReportTemplateDTO {

    /** ID（编辑时必填） */
    @Schema(description = "模板 ID")
    private Long id;

    /** 模板名称 */
    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", required = true)
    private String templateName;

    /** 模板编码 */
    @NotBlank(message = "模板编码不能为空")
    @Schema(description = "模板编码", required = true)
    private String templateCode;

    /** 模板类型 */
    @Schema(description = "模板类型：emission/immunity/general")
    private String templateType;

    /** 适用产品类别 */
    @Schema(description = "适用产品类别：ITE/Audio/Industrial/Medical/Auto")
    private String productCategory;

    /** 模板内容 */
    @Schema(description = "模板内容（JSON 格式）")
    private String templateContent;

    /** 模板预览图 URL */
    @Schema(description = "模板预览图 URL")
    private String previewUrl;

    /** 状态 */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-停用，1-启用", required = true)
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
