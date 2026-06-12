package com.emclims.module.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 统计概览 DTO
 */
@Data
@Schema(description = "统计概览")
public class StatisticsOverviewDTO {

    @Schema(description = "客户总数")
    private Integer customerTotal;

    @Schema(description = "样品总数")
    private Integer sampleTotal;

    @Schema(description = "待测样品数")
    private Integer samplePending;

    @Schema(description = "测试中样品数")
    private Integer sampleTesting;

    @Schema(description = "已完成样品数")
    private Integer sampleCompleted;

    @Schema(description = "测试计划总数")
    private Integer testPlanTotal;

    @Schema(description = "测试中计划数")
    private Integer testPlanTesting;

    @Schema(description = "已完成计划数")
    private Integer testPlanCompleted;

    @Schema(description = "报告总数")
    private Integer reportTotal;

    @Schema(description = "待审核报告数")
    private Integer reportReviewing;

    @Schema(description = "已签发报告数")
    private Integer reportIssued;

    @Schema(description = "设备总数")
    private Integer equipmentTotal;

    @Schema(description = "正常设备数")
    private Integer equipmentNormal;

    @Schema(description = "校准中设备数")
    private Integer equipmentCalibrating;

    @Schema(description = "人员总数")
    private Integer personnelTotal;

    @Schema(description = "授权有效人员数")
    private Integer personnelValid;

    @Schema(description = "本月新增样品数")
    private Integer sampleThisMonth;

    @Schema(description = "本月签发报告数")
    private Integer reportIssuedThisMonth;

    @Schema(description = "即将到期的校准设备")
    private Integer equipmentCalibrationDue;

    @Schema(description = "待更新资质人员")
    private Integer personnelAuthExpiring;

    @Schema(description = "最近 7 天样品接收趋势")
    private List<Integer> sampleTrend7Days;

    @Schema(description = "最近 7 天报告签发趋势")
    private List<Integer> reportTrend7Days;

    @Schema(description = "各类别样品分布")
    private List<Map<String, Object>> sampleCategoryDistribution;

    @Schema(description = "各类别测试分布")
    private List<Map<String, Object>> testCategoryDistribution;
}
