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
 * 测试数据记录实体
 * 对应数据库 test_record 表
 */
@TableName("test_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class TestRecord extends BaseEntity {

    /** 测试计划ID */
    private Long testPlanId;

    /** 测试项目ID */
    private Long testItemId;

    /** 测试人员ID */
    private Long testerId;

    /** 测试日期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime testDate;

    /** 结果：pass-通过，fail-不通过，na-不适用 */
    private String result;

    /** 测量值 */
    private String measurementValue;

    /** 限值 */
    private String limitValue;

    /** 余量 */
    private BigDecimal margin;

    /** 仪器ID */
    private Long instrumentId;

    /** 测试条件 */
    private String testCondition;

    /** 环境条件（JSONB格式） */
    @JsonProperty("environment")
    private String environment;

    /** 备注 */
    private String remarks;
}
