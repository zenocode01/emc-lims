package com.emclims.module.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 报告模板 VO
 */
@Data
public class ReportTemplateVO {

    /** ID */
    @Schema(description = "ID")
    private Long id;

    /** 模板名称 */
    @Schema(description = "模板名称")
    private String templateName;

    /** 模板编码 */
    @Schema(description = "模板编码")
    private String templateCode;

    /** 模板类型 */
    @Schema(description = "模板类型")
    private String templateType;

    /** 模板类型名称 */
    @Schema(description = "模板类型名称")
    private String templateTypeName;

    /** 适用产品类别 */
    @Schema(description = "适用产品类别")
    private String productCategory;

    /** 状态 */
    @Schema(description = "状态")
    private Integer status;

    /** 状态名称 */
    @Schema(description = "状态名称")
    private String statusName;

    /** 模板内容 */
    @Schema(description = "模板内容")
    private String templateContent;

    /** 模板预览图 URL */
    @Schema(description = "模板预览图 URL")
    private String previewUrl;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private String updateTime;
}
