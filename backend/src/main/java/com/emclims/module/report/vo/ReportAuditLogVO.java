package com.emclims.module.report.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报告审核日志视图对象
 */
@Data
public class ReportAuditLogVO {

    private Long id;

    /** 报告编号 */
    private String reportNo;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人 */
    private String operatorName;

    /** 操作类型：create-创建, review-审核, approve-批准, reject-打回 */
    private String action;

    /** 操作类型名称 */
    private String actionName;

    /** 审核意见 */
    private String comment;

    /** 审核时间 */
    private LocalDateTime auditTime;
}
