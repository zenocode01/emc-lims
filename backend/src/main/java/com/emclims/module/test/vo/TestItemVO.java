package com.emclims.module.test.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 测试项目 VO
 */
@Data
public class TestItemVO {
    private Long id;
    private String code;
    private String name;
    private String standard;
    private String method;
    private String category;
    private String categoryName;
    private String limitValue;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
