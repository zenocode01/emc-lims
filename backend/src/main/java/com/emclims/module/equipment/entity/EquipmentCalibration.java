package com.emclims.module.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备校准记录实体
 * 对应数据库 equipment_calibration 表
 */
@TableName("equipment_calibration")
@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentCalibration extends BaseEntity {

    /** 设备ID */
    private Long equipmentId;

    /** 校准日期 */
    private LocalDate calibrationDate;

    /** 下次校准日期 */
    private LocalDate dueDate;

    /** 校准机构 */
    private String calibrationOrg;

    /** 校准证书编号 */
    private String certificateNo;

    /** 校准结果 */
    private String result;

    /** 附件（文件路径） */
    private String attachment;
}
