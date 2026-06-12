package com.emclims.module.standard.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 标准类型枚举
 */
public enum StandardTypeEnum implements IEnum<String> {

    /** 发射 */
    EMISSION("emission", "发射"),

    /** 抗扰度 */
    IMMUNITY("immunity", "抗扰度");

    private final String value;
    private final String label;

    StandardTypeEnum(String value, String label) {
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
    public static StandardTypeEnum fromValue(String value) {
        for (StandardTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
