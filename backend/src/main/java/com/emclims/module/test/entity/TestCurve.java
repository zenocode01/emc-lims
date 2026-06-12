package com.emclims.module.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.emclims.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试曲线数据实体
 * 对应数据库 test_curve 表
 */
@TableName("test_curve")
@Data
@EqualsAndHashCode(callSuper = true)
public class TestCurve extends BaseEntity {

    /** 测试记录ID */
    private Long testRecordId;

    /** 频率 */
    private BigDecimal frequency;

    /** 幅值 */
    private BigDecimal amplitude;

    /** 限值 */
    private BigDecimal limit;

    /** 余量 */
    private BigDecimal margin;

    /** 标记点（JSONB格式） */
    @JsonProperty("markerPoints")
    private String markerPoints;
}
