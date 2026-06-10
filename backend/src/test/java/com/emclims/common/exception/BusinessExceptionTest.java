package com.emclims.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BusinessException 业务异常单元测试
 */
class BusinessExceptionTest {

    @Test
    void testExceptionWithMessage() {
        BusinessException ex = new BusinessException("用户不存在");
        assertEquals("用户不存在", ex.getMessage());
        assertEquals(500, ex.getCode());
    }

    @Test
    void testExceptionWithCodeAndMessage() {
        BusinessException ex = new BusinessException(400, "参数错误");
        assertEquals("参数错误", ex.getMessage());
        assertEquals(400, ex.getCode());
    }

    @Test
    void testExceptionWithCause() {
        Throwable cause = new RuntimeException("底层错误");
        BusinessException ex = new BusinessException("业务失败", cause);
        assertEquals("业务失败", ex.getMessage());
        assertEquals(500, ex.getCode());
        assertNotNull(ex.getCause());
        assertEquals("底层错误", ex.getCause().getMessage());
    }
}
