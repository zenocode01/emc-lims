package com.emclims.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DataPermissionContext ThreadLocal 数据权限上下文单元测试
 * 验证用户ID、部门ID、数据权限范围的 set/get/clear 行为
 */
class DataPermissionContextTest {

    @AfterEach
    void tearDown() {
        DataPermissionContext.clear();
    }

    @Test
    void testSetAndGetUserId() {
        assertNull(DataPermissionContext.getUserId());
        DataPermissionContext.setUserId(100L);
        assertEquals(100L, DataPermissionContext.getUserId());
    }

    @Test
    void testSetAndGetDeptId() {
        assertNull(DataPermissionContext.getDeptId());
        DataPermissionContext.setDeptId(200L);
        assertEquals(200L, DataPermissionContext.getDeptId());
    }

    @Test
    void testSetAndGetDataScope() {
        assertNull(DataPermissionContext.getDataScope());
        DataPermissionContext.setDataScope(2);
        assertEquals(2, DataPermissionContext.getDataScope());
    }

    @Test
    void testClear() {
        DataPermissionContext.setUserId(100L);
        DataPermissionContext.setDeptId(200L);
        DataPermissionContext.setDataScope(2);

        DataPermissionContext.clear();

        assertNull(DataPermissionContext.getUserId());
        assertNull(DataPermissionContext.getDeptId());
        assertNull(DataPermissionContext.getDataScope());
    }

    @Test
    void testThreadIsolation() throws InterruptedException {
        DataPermissionContext.setUserId(1L);
        DataPermissionContext.setDataScope(1);

        Thread otherThread = new Thread(() -> {
            // 另一个线程应该看不到主线程的值
            assertNull(DataPermissionContext.getUserId());
            assertNull(DataPermissionContext.getDataScope());

            // 设置自己的值
            DataPermissionContext.setUserId(2L);
            DataPermissionContext.setDataScope(4);
            assertEquals(2L, DataPermissionContext.getUserId());
            assertEquals(4, DataPermissionContext.getDataScope());

            DataPermissionContext.clear();
            assertNull(DataPermissionContext.getUserId());
        });

        otherThread.start();
        otherThread.join(1000);

        // 主线程的值应该不受影响
        assertEquals(1L, DataPermissionContext.getUserId());
        assertEquals(1, DataPermissionContext.getDataScope());
    }

    @Test
    void testSetUserIdOverride() {
        DataPermissionContext.setUserId(1L);
        assertEquals(1L, DataPermissionContext.getUserId());

        DataPermissionContext.setUserId(2L);
        assertEquals(2L, DataPermissionContext.getUserId());
    }

    @Test
    void testSetNullValues() {
        DataPermissionContext.setUserId(1L);
        DataPermissionContext.setDeptId(2L);
        DataPermissionContext.setDataScope(3);

        DataPermissionContext.setUserId(null);
        DataPermissionContext.setDeptId(null);
        DataPermissionContext.setDataScope(null);

        assertNull(DataPermissionContext.getUserId());
        assertNull(DataPermissionContext.getDeptId());
        assertNull(DataPermissionContext.getDataScope());
    }
}
