package com.emclims.module.report.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 报告状态枚举
 */
public enum ReportStatusEnum implements IEnum<String> {

    /** 草稿 */
    DRAFT("draft", "草稿"),
    /** 审核中 */
    REVIEW("review", "审核中"),
    /** 已批准 */
    APPROVED("approved", "已批准"),
    /** 已签发 */
    ISSUED("issued", "已签发"),
    /** 已打回 */
    REJECTED("rejected", "已打回");

    private final String value;
    private final String label;

    ReportStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据值获取枚举
     */
    public static ReportStatusEnum fromValue(String value) {
        for (ReportStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return DRAFT;
    }
}
