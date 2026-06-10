package com.emclims.common.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseEntity 实体基类单元测试
 */
class BaseEntityTest {

    @Test
    void testBaseEntityFields() {
        // 使用匿名子类测试基类字段
        BaseEntity entity = new BaseEntity() {};
        entity.setId(1L);
        entity.setCreateTime(LocalDateTime.of(2026, 6, 10, 10, 0));
        entity.setUpdateTime(LocalDateTime.of(2026, 6, 10, 12, 0));
        entity.setCreateBy(100L);
        entity.setUpdateBy(200L);
        entity.setDeleted(0);
        entity.setRemark("测试备注");

        assertEquals(1L, entity.getId());
        assertEquals(LocalDateTime.of(2026, 6, 10, 10, 0), entity.getCreateTime());
        assertEquals(LocalDateTime.of(2026, 6, 10, 12, 0), entity.getUpdateTime());
        assertEquals(100L, entity.getCreateBy());
        assertEquals(200L, entity.getUpdateBy());
        assertEquals(0, entity.getDeleted());
        assertEquals("测试备注", entity.getRemark());
    }

    @Test
    void testDefaultValues() {
        BaseEntity entity = new BaseEntity() {};
        assertNull(entity.getId());
        assertNull(entity.getDeleted());
        assertNull(entity.getRemark());
    }
}
