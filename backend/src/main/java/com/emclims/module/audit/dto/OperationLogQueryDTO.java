package com.emclims.module.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询 DTO
 */
@Data
public class OperationLogQueryDTO {

    /** 操作人 ID */
    @Schema(description = "操作人 ID")
    private Long operatorId;

    /** 操作模块 */
    @Schema(description = "操作模块：user/role/customer/sample 等")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型：create/update/delete 等")
    private String action;

    /** 操作开始时间 */
    @Schema(description = "操作开始时间")
    private LocalDateTime startTime;

    /** 操作结束时间 */
    @Schema(description = "操作结束时间")
    private LocalDateTime endTime;

    /** 页码 */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /** 每页条数 */
    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
