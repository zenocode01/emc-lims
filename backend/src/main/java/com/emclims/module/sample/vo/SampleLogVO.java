package com.emclims.module.sample.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样品流转日志视图对象
 */
@Data
public class SampleLogVO {

    private Long id;

    /** 样品ID */
    private Long sampleId;

    /** 来源状态 */
    private String fromStatus;

    /** 来源状态名称 */
    private String fromStatusName;

    /** 目标状态 */
    private String toStatus;

    /** 目标状态名称 */
    private String toStatusName;

    /** 操作人 */
    private Long operator;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作时间 */
    private LocalDateTime operateTime;

    /** 备注 */
    private String remark;
}
