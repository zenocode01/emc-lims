package com.emclims.module.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 标准视图对象
 */
@Data
@Schema(description = "标准视图对象")
public class StandardVO {

    /** ID */
    @Schema(description = "ID")
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

    /** 发布机构 */
    @Schema(description = "发布机构")
    private String issuingOrg;

    /** 生效日期 */
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    /** 失效日期 */
    @Schema(description = "失效日期")
    private LocalDate expiryDate;

    /** 状态码 */
    @Schema(description = "状态码（0-禁用，1-启用）")
    private String status;

    /** 状态名称 */
    @Schema(description = "状态名称")
    private String statusName;

    /** 标准类型码 */
    @Schema(description = "标准类型码（emission-发射，immunity-抗扰度）")
    private String type;

    /** 标准类型名称 */
    @Schema(description = "标准类型名称")
    private String typeName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
