package com.emclims.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult 分页结果单元测试
 */
class PageResultTest {

    @Test
    void testOf() {
        Page<String> page = new Page<>(1, 10, 100);
        page.setRecords(Arrays.asList("a", "b"));

        PageResult<String> result = PageResult.of(page);
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(100, result.getTotal());
        assertEquals(10, result.getPages()); // ceil(100/10)
        assertEquals(2, result.getRecords().size());
    }

    @Test
    void testEmptyPage() {
        Page<String> page = new Page<>(1, 10, 0);
        page.setRecords(List.of());

        PageResult<String> result = PageResult.of(page);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
    }

    @Test
    void testSinglePage() {
        Page<String> page = new Page<>(1, 10, 3);
        page.setRecords(Arrays.asList("x", "y", "z"));

        PageResult<String> result = PageResult.of(page);
        assertEquals(1, result.getPages());
        assertEquals(3, result.getRecords().size());
    }
}
