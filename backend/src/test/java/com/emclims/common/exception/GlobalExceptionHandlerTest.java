package com.emclims.common.exception;

import com.emclims.common.response.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GlobalExceptionHandler 全局异常处理器单元测试
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleBusinessException() {
        BusinessException ex = new BusinessException(400, "手机号已存在");
        R<?> result = handler.handleBusinessException(ex);
        assertEquals(400, result.getCode());
        assertEquals("手机号已存在", result.getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("数据库连接失败");
        R<?> result = handler.handleException(ex);
        assertEquals(500, result.getCode());
        assertEquals("系统异常，请联系管理员", result.getMessage());
    }
}
