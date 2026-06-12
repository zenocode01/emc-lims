package com.emclims.module.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 报告审核日志实体
 * 对应数据库 report_audit_log 表
 */
@TableName("report_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportAuditLog extends BaseEntity {

    /** 报告ID */
    private Long reportId;

    /** 操作人ID */
    private Long operatorId;

    /** 操作类型：create-创建, review-审核, approve-批准, reject-打回 */
    private String action;

    /** 审核意见 */
    private String comment;

    /** 审核时间 */
    private LocalDateTime auditTime;
}
