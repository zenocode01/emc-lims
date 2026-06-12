package com.emclims.module.audit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志 VO
 */
@Data
public class OperationLogVO {

    /** ID */
    @Schema(description = "ID")
    private Long id;

    /** 操作人 ID */
    @Schema(description = "操作人 ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 操作模块 */
    @Schema(description = "操作模块")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型")
    private String action;

    /** 操作描述 */
    @Schema(description = "操作描述")
    private String description;

    /** 请求 IP */
    @Schema(description = "请求 IP")
    private String requestIp;

    /** 请求 URL */
    @Schema(description = "请求 URL")
    private String requestUrl;

    /** 请求方法 */
    @Schema(description = "请求方法")
    private String requestMethod;

    /** 响应状态码 */
    @Schema(description = "响应状态码")
    private Integer responseCode;

    /** 响应耗时（毫秒） */
    @Schema(description = "响应耗时")
    private Long responseTime;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    /** 模块名称 */
    @Schema(description = "模块名称")
    private String moduleName;

    /** 操作类型名称 */
    @Schema(description = "操作类型名称")
    private String actionName;
}
