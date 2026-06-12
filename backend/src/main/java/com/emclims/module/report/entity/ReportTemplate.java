package com.emclims.module.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报告模板实体
 * 对应数据库 report_template 表
 */
@TableName("report_template")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportTemplate extends BaseEntity {

    /** 模板名称 */
    private String templateName;

    /** 模板编码 */
    private String templateCode;

    /** 模板类型：emission-发射，immunity-抗扰度，general-通用 */
    private String templateType;

    /** 适用产品类别：ITE-信息技术设备，Audio-音频设备，Industrial-工业设备，Medical-医疗设备，Auto-汽车电子 */
    private String productCategory;

    /** 模板内容（HTML/JSON 格式） */
    private String templateContent;

    /** 模板预览图 URL */
    private String previewUrl;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
