package com.emclims.common.config;

import com.emclims.common.security.PermissionInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionConfig 权限配置单元测试
 */
class PermissionConfigTest {

    @Test
    void testBeanCreation() {
        PermissionInterceptor interceptor = new PermissionInterceptor();
        PermissionConfig config = new PermissionConfig(interceptor);
        assertNotNull(config);
    }

    @Test
    void testNullInterceptor() {
        PermissionConfig config = new PermissionConfig(null);
        assertNotNull(config);
    }
}
