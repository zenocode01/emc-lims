package com.emclims.module.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 报告模板查询 DTO
 */
@Data
public class ReportTemplateQueryDTO {

    /** 关键字搜索 */
    @Schema(description = "关键字搜索")
    private String keyword;

    /** 模板类型 */
    @Schema(description = "模板类型：emission/immunity/general")
    private String templateType;

    /** 适用产品类别 */
    @Schema(description = "适用产品类别")
    private String productCategory;

    /** 状态 */
    @Schema(description = "状态：0-停用，1-启用")
    private Integer status;

    /** 页码 */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /** 每页条数 */
    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
