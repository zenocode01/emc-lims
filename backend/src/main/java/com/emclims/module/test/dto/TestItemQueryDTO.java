package com.emclims.module.test.dto;

import lombok.Data;

/**
 * 测试项目查询 DTO
 */
@Data
public class TestItemQueryDTO {

    /** 搜索关键字（编号或名称） */
    private String keyword;

    /** 类别 */
    private String category;

    /** 状态 */
    private Integer status;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
