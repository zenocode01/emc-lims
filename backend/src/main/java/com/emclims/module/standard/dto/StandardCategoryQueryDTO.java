package com.emclims.module.standard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标准分类查询 DTO
 */
@Data
@Schema(description = "标准分类查询DTO")
public class StandardCategoryQueryDTO {

    /** 搜索关键字（名称/产品类型） */
    @Schema(description = "搜索关键字（名称/产品类型）")
    private String keyword;

    /** 产品类型 */
    @Schema(description = "产品类型")
    private String productType;

    /** 页码 */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /** 每页数量 */
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
