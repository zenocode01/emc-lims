package com.emclims.module.standard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 标准分类编辑 DTO
 */
@Data
@Schema(description = "标准分类编辑DTO")
public class StandardCategoryDTO {

    /** ID（更新时必填） */
    @Schema(description = "ID，更新时必填")
    private Long id;

    /** 分类名称 */
    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "EMS抗扰度标准")
    private String name;

    /** 适用标准列表（标准ID列表） */
    @Schema(description = "适用标准列表（标准ID列表）")
    private List<Long> applicableStandards;

    /** 产品类型 */
    @NotBlank(message = "产品类型不能为空")
    @Schema(description = "产品类型", example = "Class A")
    private String productType;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
