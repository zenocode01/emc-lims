package com.emclims.module.sample.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 样品状态枚举
 */
public enum SampleStatusEnum implements IEnum<String> {

    /** 待收样 */
    PENDING("pending", "待收样"),
    /** 已收样 */
    RECEIVED("received", "已收样"),
    /** 测试中 */
    TESTING("testing", "测试中"),
    /** 测试完成 */
    COMPLETED("completed", "测试完成"),
    /** 留样中 */
    RETAINED("retained", "留样中"),
    /** 已处置 */
    DISPOSED("disposed", "已处置"),
    /** 已归还 */
    RETURNED("returned", "已归还");

    private final String value;
    private final String label;

    SampleStatusEnum(String value, String label) {
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

    public static SampleStatusEnum fromValue(String value) {
        for (SampleStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
