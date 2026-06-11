package com.emclims.module.sample.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样品流转日志实体
 * 对应数据库 sample_log 表
 */
@TableName("sample_log")
@Data
public class SampleLog {

    /** 主键ID */
    private Long id;

    /** 样品ID */
    private Long sampleId;

    /** 来源状态 */
    private String fromStatus;

    /** 目标状态 */
    private String toStatus;

    /** 操作人 */
    private Long operator;

    /** 操作时间 */
    private LocalDateTime operateTime;

    /** 备注 */
    private String remark;
}
