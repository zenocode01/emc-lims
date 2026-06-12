package com.emclims.module.equipment.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用记录视图对象
 */
@Data
public class UsageVO {

    /** 使用记录ID */
    private Long id;

    /** 设备ID */
    private Long equipmentId;

    /** 设备编号 */
    private String equipmentNo;

    /** 设备名称 */
    private String equipmentName;

    /** 测试计划ID */
    private Long testPlanId;

    /** 使用人ID */
    private Long userId;

    /** 使用人姓名 */
    private String userName;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 状态码 */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 创建时间 */
    private LocalDateTime createTime;
}
