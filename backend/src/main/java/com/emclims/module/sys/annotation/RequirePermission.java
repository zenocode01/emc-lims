package com.emclims.module.sys.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于方法级别的权限校验
 * 用法：@RequirePermission("sys:user:add")
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识
     */
    String value();

    /**
     * 权限验证模式
     * ALL：需要所有权限（AND 逻辑）
     * ANY：需要任一权限（OR 逻辑）
     */
    PermissionMode mode() default PermissionMode.ANY;

    enum PermissionMode {
        ALL,  // 所有权限都需要
        ANY   // 任一权限即可
    }
}
