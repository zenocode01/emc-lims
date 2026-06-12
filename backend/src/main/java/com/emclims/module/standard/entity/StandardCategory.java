package com.emclims.module.standard.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 标准分类实体
 * 对应数据库 standard_category 表
 */
@TableName("standard_category")
@Data
@EqualsAndHashCode(callSuper = true)
public class StandardCategory extends BaseEntity {

    /** 分类名称 */
    private String name;

    /** 适用标准列表（JSONB 格式，存储标准ID列表） */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<Long> applicableStandards;

    /** 产品类型 */
    private String productType;
}
