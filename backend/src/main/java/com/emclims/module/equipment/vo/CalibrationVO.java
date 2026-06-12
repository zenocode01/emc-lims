package com.emclims.module.equipment.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 校准记录视图对象
 */
@Data
public class CalibrationVO {

    /** 校准记录ID */
    private Long id;

    /** 设备ID */
    private Long equipmentId;

    /** 设备编号 */
    private String equipmentNo;

    /** 设备名称 */
    private String equipmentName;

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

    /** 创建时间 */
    private LocalDateTime createTime;
}
