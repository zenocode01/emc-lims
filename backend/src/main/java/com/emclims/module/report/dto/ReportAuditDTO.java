package com.emclims.module.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报告审核操作 DTO
 */
@Data
public class ReportAuditDTO {

    /** 报告ID */
    @NotNull(message = "报告ID不能为空")
    private Long reportId;

    /** 审核意见 */
    private String comment;
}
