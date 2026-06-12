package com.emclims.module.report.enums;

/**
 * 报告审核操作类型枚举
 */
public enum ReportActionEnum {

    /** 创建 */
    CREATE("create", "创建"),
    /** 审核 */
    REVIEW("review", "审核"),
    /** 批准 */
    APPROVE("approve", "批准"),
    /** 打回 */
    REJECT("reject", "打回");

    private final String value;
    private final String label;

    ReportActionEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据值获取枚举
     */
    public static ReportActionEnum fromValue(String value) {
        for (ReportActionEnum action : values()) {
            if (action.value.equals(value)) {
                return action;
            }
        }
        return CREATE;
    }
}
