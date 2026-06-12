package com.emclims.module.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标准分类视图对象
 */
@Data
@Schema(description = "标准分类视图对象")
public class StandardCategoryVO {

    /** ID */
    @Schema(description = "ID")
    private Long id;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String name;

    /** 适用标准ID列表 */
    @Schema(description = "适用标准ID列表")
    private List<Long> applicableStandards;

    /** 适用标准详细信息列表（含编号和名称） */
    @Schema(description = "适用标准详细信息列表")
    private List<StandardCategoryVO.StandardInfo> applicableStandardDetails;

    /** 产品类型 */
    @Schema(description = "产品类型")
    private String productType;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 标准基本信息（用于前端展示）
     */
    @Data
    @Schema(description = "标准基本信息")
    public static class StandardInfo {
        /** 标准ID */
        @Schema(description = "标准ID")
        private Long id;

        /** 标准编号 */
        @Schema(description = "标准编号")
        private String code;

        /** 标准名称 */
        @Schema(description = "标准名称")
        private String name;

        /** 版本号 */
        @Schema(description = "版本号")
        private String version;
    }
}
