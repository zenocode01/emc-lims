package com.emclims.module.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.emclims.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 测试计划实体
 * 对应数据库 test_plan 表
 */
@TableName("test_plan")
@Data
@EqualsAndHashCode(callSuper = true)
public class TestPlan extends BaseEntity {

    /** 计划编号 */
    private String planNo;

    /** 样品ID */
    private Long sampleId;

    /** 客户ID */
    private Long customerId;

    /** 测试项目配置（JSONB格式，包含项目ID和限值配置） */
    @JsonProperty("testItems")
    private String testItems;

    /** 状态：draft-草稿，testing-测试中，completed-已完成，cancelled-已取消 */
    private String status;

    /** 计划日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /** 备注 */
    private String remark;
}
