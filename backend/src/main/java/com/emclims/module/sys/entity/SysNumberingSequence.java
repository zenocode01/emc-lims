package com.emclims.module.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 编号序列日计数器实体
 */
@Data
@TableName("sys_numbering_sequence")
public class SysNumberingSequence {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 规则编码 */
    private String ruleCode;

    /** 业务日期 */
    private LocalDate bizDate;

    /** 当前已使用到的序列号 */
    private Integer currentSeq;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
