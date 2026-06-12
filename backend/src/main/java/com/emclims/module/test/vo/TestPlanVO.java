package com.emclims.module.test.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试计划 VO
 */
@Data
public class TestPlanVO {
    private Long id;
    private String planNo;
    private Long sampleId;
    private String sampleNo;
    private String productName;
    private Long customerId;
    private String customerName;
    private List<Object> testItems;
    private String status;
    private String statusName;
    private LocalDate planDate;
    private LocalDate dueDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
