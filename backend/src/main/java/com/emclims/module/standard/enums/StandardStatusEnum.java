package com.emclims.module.standard.enums;

/**
 * 标准状态枚举
 */
public enum StandardStatusEnum {

    /** 禁用 */
    DISABLED("0", "禁用"),

    /** 启用 */
    ENABLED("1", "启用");

    private final String value;
    private final String label;

    StandardStatusEnum(String value, String label) {
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
    public static StandardStatusEnum fromValue(String value) {
        for (StandardStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
