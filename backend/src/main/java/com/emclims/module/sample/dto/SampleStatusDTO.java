package com.emclims.module.sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 样品状态变更 DTO
 */
@Data
public class SampleStatusDTO {

    @NotNull(message = "样品ID不能为空")
    private Long sampleId;

    @NotBlank(message = "目标状态不能为空")
    private String toStatus;

    /** 备注 */
    private String remark;
}
