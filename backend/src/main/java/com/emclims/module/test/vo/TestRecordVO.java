package com.emclims.module.test.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测试记录 VO
 */
@Data
public class TestRecordVO {
    private Long id;
    private Long testPlanId;
    private String planNo;
    private String sampleNo;
    private String productName;
    private Long testItemId;
    private String testItemCode;
    private String testItemName;
    private String testItemCategory;
    private Long testerId;
    private String testerName;
    private LocalDateTime testDate;
    private String result;
    private String resultName;
    private String measurementValue;
    private String limitValue;
    private BigDecimal margin;
    private Long instrumentId;
    private String instrumentName;
    private String testCondition;
    private String environment;
    private String remarks;
    private LocalDateTime createTime;
}
