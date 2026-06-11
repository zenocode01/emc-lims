package com.emclims.module.sample.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 留样记录实体
 * 对应数据库 sample_retention 表
 */
@TableName("sample_retention")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleRetention extends BaseEntity {

    /** 样品ID */
    private Long sampleId;

    /** 留样日期 */
    private LocalDate retentionDate;

    /** 存储位置 */
    private String storageLocation;

    /** 状态：retained-留样中，disposed-已处置，returned-已归还 */
    private String status;

    /** 处置日期 */
    private LocalDate dispositionDate;

    /** 处置方式 */
    private String dispositionMethod;

    /** 处置人 */
    private Long dispositionPerson;
}
