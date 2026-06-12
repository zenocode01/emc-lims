package com.emclims.module.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.emclims.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 测试项目定义实体
 * 对应数据库 test_item 表
 */
@TableName("test_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class TestItem extends BaseEntity {

    /** 项目编号 */
    private String code;

    /** 项目名称 */
    private String name;

    /** 所属标准 */
    private String standard;

    /** 测试方法 */
    private String method;

    /** 类别：emission-发射，immunity-抗扰度 */
    private String category;

    /** 限值配置（JSONB格式） */
    @JsonProperty("limitValue")
    private String limitValue;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
