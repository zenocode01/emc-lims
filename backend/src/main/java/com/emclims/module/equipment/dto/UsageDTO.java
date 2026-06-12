package com.emclims.module.equipment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用记录新增/编辑 DTO
 */
@Data
public class UsageDTO {

    /** 使用记录ID（编辑时必填） */
    private Long id;

    /** 设备ID */
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    /** 测试计划ID */
    private Long testPlanId;

    /** 使用人ID */
    @NotNull(message = "使用人ID不能为空")
    private Long userId;

    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 状态：in_use-使用中，completed-已完成 */
    private String status;

    /** 备注 */
    private String remark;
}
