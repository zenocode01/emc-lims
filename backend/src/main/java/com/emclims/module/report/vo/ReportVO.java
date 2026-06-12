package com.emclims.module.report.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报告视图对象
 */
@Data
public class ReportVO {

    private Long id;

    /** 报告编号 */
    private String reportNo;

    /** 样品ID */
    private Long sampleId;

    /** 样品编号 */
    private String sampleNo;

    /** 客户ID */
    private Long customerId;

    /** 客户名称 */
    private String customerName;

    /** 报告状态码 */
    private String status;

    /** 报告状态名称 */
    private String statusName;

    /** 版本号 */
    private Integer version;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核人 */
    private String reviewerName;

    /** 批准人ID */
    private Long approverId;

    /** 批准人 */
    private String approverName;

    /** 签发日期 */
    private LocalDate issuedDate;

    /** 报告文件URL */
    private String fileUrl;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
