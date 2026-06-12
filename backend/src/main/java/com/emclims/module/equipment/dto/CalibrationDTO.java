package com.emclims.module.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 校准记录新增/编辑 DTO
 */
@Data
public class CalibrationDTO {

    /** 校准记录ID（编辑时必填） */
    private Long id;

    /** 设备ID */
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    /** 校准日期 */
    @NotNull(message = "校准日期不能为空")
    private LocalDate calibrationDate;

    /** 下次校准日期 */
    private LocalDate dueDate;

    /** 校准机构 */
    @NotBlank(message = "校准机构不能为空")
    private String calibrationOrg;

    /** 校准证书编号 */
    private String certificateNo;

    /** 校准结果 */
    private String result;

    /** 附件（文件路径） */
    private String attachment;
}
