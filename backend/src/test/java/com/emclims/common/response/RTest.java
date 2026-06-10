package com.emclims.common.response;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * R 响应工具类单元测试
 */
class RTest {

    @Test
    void testOkWithData() {
        R<String> result = R.ok("test data");
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void testOkWithMessageAndData() {
        R<Integer> result = R.ok("自定义消息", 42);
        assertEquals(200, result.getCode());
        assertEquals("自定义消息", result.getMessage());
        assertEquals(42, result.getData());
    }

    @Test
    void testFail() {
        R<?> result = R.fail();
        assertEquals(500, result.getCode());
        assertEquals("服务器内部错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailWithMessage() {
        R<?> result = R.fail("业务处理失败");
        assertEquals(500, result.getCode());
        assertEquals("业务处理失败", result.getMessage());
    }

    @Test
    void testFailWithCodeAndMessage() {
        R<?> result = R.fail(400, "参数错误");
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
    }

    @Test
    void testGenericListData() {
        List<String> list = Arrays.asList("a", "b", "c");
        R<List<String>> result = R.ok(list);
        assertEquals(200, result.getCode());
        assertEquals(3, result.getData().size());
        assertTrue(result.getData().contains("a"));
    }

    @Test
    void testNullData() {
        R<String> result = R.ok((String) null);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }
}
