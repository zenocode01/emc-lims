package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编号规则配置实体
 */
@TableName("sys_numbering_rule")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNumberingRule extends BaseEntity {

    /** 规则编码（唯一） */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 模块类型（sample-样品, report-报告, contract-合同等） */
    private String moduleType;

    /** 前缀 */
    private String prefix;

    /** 日期格式，如 yyyyMMdd */
    private String datePattern;

    /** 流水号长度，如 4 表示 0001 */
    private Integer seqLength;

    /** 分隔符 */
    private String separator;

    /** 描述 */
    private String description;

    /** 状态（1-启用，0-禁用） */
    private Integer status;
}
