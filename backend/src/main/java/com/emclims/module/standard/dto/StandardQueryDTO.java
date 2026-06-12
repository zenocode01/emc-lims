package com.emclims.module.standard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 标准查询 DTO
 */
@Data
@Schema(description = "标准查询DTO")
public class StandardQueryDTO {

    /** 搜索关键字（编号/名称） */
    @Schema(description = "搜索关键字（编号/名称）")
    private String keyword;

    /** 标准类型 */
    @Schema(description = "标准类型（emission-发射，immunity-抗扰度）")
    private String type;

    /** 状态 */
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;

    /** 生效日期起 */
    @Schema(description = "生效日期起", example = "2020-01-01")
    private LocalDate effectiveDateStart;

    /** 生效日期止 */
    @Schema(description = "生效日期止", example = "2026-12-31")
    private LocalDate effectiveDateEnd;

    /** 页码 */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /** 每页数量 */
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
