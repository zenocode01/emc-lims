package com.emclims.module.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 设备使用记录实体
 * 对应数据库 equipment_usage 表
 */
@TableName("equipment_usage")
@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentUsage extends BaseEntity {

    /** 设备ID */
    private Long equipmentId;

    /** 测试计划ID */
    private Long testPlanId;

    /** 使用人ID */
    private Long userId;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 状态：in_use-使用中，completed-已完成 */
    private String status;
}
