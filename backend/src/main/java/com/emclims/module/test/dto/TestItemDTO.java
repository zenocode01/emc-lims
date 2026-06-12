package com.emclims.module.test.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 测试项目 DTO
 */
@Data
public class TestItemDTO {

    private Long id;

    @NotBlank(message = "测试项目编号不能为空")
    private String code;

    @NotBlank(message = "测试项目名称不能为空")
    private String name;

    private String standard;

    private String method;

    @NotBlank(message = "测试项目类别不能为空")
    private String category;

    private String limitValue;

    private String remark;
}
