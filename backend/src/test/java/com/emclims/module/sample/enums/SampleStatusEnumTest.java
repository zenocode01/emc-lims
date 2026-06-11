package com.emclims.module.sample.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleStatusEnum 枚举单元测试
 */
class SampleStatusEnumTest {

    // === fromValue 测试 ===

    @Test
    void testFromValuePending() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("pending");
        assertEquals(SampleStatusEnum.PENDING, result);
        assertEquals("pending", result.getValue());
        assertEquals("待收样", result.getLabel());
    }

    @Test
    void testFromValueReceived() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("received");
        assertEquals(SampleStatusEnum.RECEIVED, result);
        assertEquals("received", result.getValue());
        assertEquals("已收样", result.getLabel());
    }

    @Test
    void testFromValueTesting() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("testing");
        assertEquals(SampleStatusEnum.TESTING, result);
        assertEquals("testing", result.getValue());
        assertEquals("测试中", result.getLabel());
    }

    @Test
    void testFromValueCompleted() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("completed");
        assertEquals(SampleStatusEnum.COMPLETED, result);
        assertEquals("completed", result.getValue());
        assertEquals("测试完成", result.getLabel());
    }

    @Test
    void testFromValueRetained() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("retained");
        assertEquals(SampleStatusEnum.RETAINED, result);
        assertEquals("retained", result.getValue());
        assertEquals("留样中", result.getLabel());
    }

    @Test
    void testFromValueDisposed() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("disposed");
        assertEquals(SampleStatusEnum.DISPOSED, result);
        assertEquals("disposed", result.getValue());
        assertEquals("已处置", result.getLabel());
    }

    @Test
    void testFromValueReturned() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("returned");
        assertEquals(SampleStatusEnum.RETURNED, result);
        assertEquals("returned", result.getValue());
        assertEquals("已归还", result.getLabel());
    }

    @Test
    void testFromValueNotFound() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("unknown");
        assertEquals(SampleStatusEnum.PENDING, result);
    }

    @Test
    void testFromValueNull() {
        SampleStatusEnum result = SampleStatusEnum.fromValue(null);
        assertEquals(SampleStatusEnum.PENDING, result);
    }

    @Test
    void testFromValueEmpty() {
        SampleStatusEnum result = SampleStatusEnum.fromValue("");
        assertEquals(SampleStatusEnum.PENDING, result);
    }

    // === valueOf 测试 ===

    @Test
    void testValuesAll() {
        SampleStatusEnum[] values = SampleStatusEnum.values();
        assertEquals(7, values.length);
    }

    @Test
    void testValueOfPending() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("PENDING");
        assertEquals(SampleStatusEnum.PENDING, result);
    }

    @Test
    void testValueOfReceived() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("RECEIVED");
        assertEquals(SampleStatusEnum.RECEIVED, result);
    }

    @Test
    void testValueOfTesting() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("TESTING");
        assertEquals(SampleStatusEnum.TESTING, result);
    }

    @Test
    void testValueOfCompleted() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("COMPLETED");
        assertEquals(SampleStatusEnum.COMPLETED, result);
    }

    @Test
    void testValueOfRetained() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("RETAINED");
        assertEquals(SampleStatusEnum.RETAINED, result);
    }

    @Test
    void testValueOfDisposed() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("DISPOSED");
        assertEquals(SampleStatusEnum.DISPOSED, result);
    }

    @Test
    void testValueOfReturned() {
        SampleStatusEnum result = SampleStatusEnum.valueOf("RETURNED");
        assertEquals(SampleStatusEnum.RETURNED, result);
    }

    // === 业务逻辑测试 ===

    @Test
    void testLabelsUnique() {
        // 验证各状态的标签唯一
        SampleStatusEnum[] values = SampleStatusEnum.values();
        long uniqueLabels = java.util.Arrays.stream(values)
                .map(SampleStatusEnum::getLabel)
                .distinct()
                .count();
        assertEquals(7, uniqueLabels);
    }

    @Test
    void testValuesUnique() {
        // 验证各状态的值唯一
        SampleStatusEnum[] values = SampleStatusEnum.values();
        long uniqueValues = java.util.Arrays.stream(values)
                .map(SampleStatusEnum::getValue)
                .distinct()
                .count();
        assertEquals(7, uniqueValues);
    }

    // === 验证 IEnum 接口实现 ===

    @Test
    void testIEnumInterface() {
        // 验证实现了 IEnum<String> 接口，getValue() 返回正确的值
        // IEnum 接口定义了 getVal()，SampleStatusEnum 重写了 getValue()
        assertEquals("pending", SampleStatusEnum.PENDING.getValue());
        assertEquals("received", SampleStatusEnum.RECEIVED.getValue());
        assertEquals("testing", SampleStatusEnum.TESTING.getValue());
        assertEquals("completed", SampleStatusEnum.COMPLETED.getValue());
        assertEquals("retained", SampleStatusEnum.RETAINED.getValue());
        assertEquals("disposed", SampleStatusEnum.DISPOSED.getValue());
        assertEquals("returned", SampleStatusEnum.RETURNED.getValue());
    }
}
