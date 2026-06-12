package com.emclims.module.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报告编辑 DTO
 */
@Data
public class ReportDTO {

    /** 报告ID（更新时必填） */
    private Long id;

    /** 样品ID */
    @NotNull(message = "样品不能为空")
    private Long sampleId;

    /** 客户ID */
    @NotNull(message = "客户不能为空")
    private Long customerId;

    /** 版本号 */
    private Integer version;

    /** 签发日期 */
    private LocalDate issuedDate;

    /** 报告文件URL */
    private String fileUrl;

    /** 备注 */
    private String remark;
}
