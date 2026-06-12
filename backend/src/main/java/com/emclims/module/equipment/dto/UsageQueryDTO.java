package com.emclims.module.equipment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用记录查询 DTO
 */
@Data
public class UsageQueryDTO {

    /** 设备ID */
    private Long equipmentId;

    /** 使用人ID */
    private Long userId;

    /** 开始时间-开始 */
    private LocalDateTime startTimeStart;

    /** 开始时间-结束 */
    private LocalDateTime startTimeEnd;

    /** 状态 */
    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
