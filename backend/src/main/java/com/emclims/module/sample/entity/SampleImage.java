package com.emclims.module.sample.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样品照片实体
 * 对应数据库 sample_image 表
 */
@TableName("sample_image")
@Data
public class SampleImage {

    /** 主键ID */
    private Long id;

    /** 样品ID */
    private Long sampleId;

    /** 图片URL */
    private String imageUrl;

    /** 类型：photo-照片, attachment-附件 */
    private String type;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
