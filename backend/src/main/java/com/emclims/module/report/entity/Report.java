package com.emclims.module.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 报告实体
 * 对应数据库 report 表
 */
@TableName("report")
@Data
@EqualsAndHashCode(callSuper = true)
public class Report extends BaseEntity {

    /** 报告编号 */
    private String reportNo;

    /** 样品ID */
    private Long sampleId;

    /** 客户ID */
    private Long customerId;

    /** 报告状态：draft-草稿, review-审核中, approved-已批准, issued-已签发, rejected-已打回 */
    private String status;

    /** 版本号 */
    private Integer version;

    /** 审核人ID */
    private Long reviewerId;

    /** 批准人ID */
    private Long approverId;

    /** 签发日期 */
    private LocalDate issuedDate;

    /** 报告文件URL */
    private String fileUrl;
}
