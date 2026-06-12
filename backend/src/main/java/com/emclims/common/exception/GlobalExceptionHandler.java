package com.emclims.common.exception;

import com.emclims.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<?> handleValidException(Exception e) {
        log.error("参数校验异常: {}", e.getMessage());
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst()
                    .orElse("参数校验失败");
        } else if (e instanceof BindException ex) {
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst()
                    .orElse("参数校验失败");
        }
        return R.fail(400, message);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail("系统异常，请联系管理员");
    }
}
