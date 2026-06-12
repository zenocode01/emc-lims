package com.emclims.module.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.test.dto.TestItemDTO;
import com.emclims.module.test.dto.TestItemQueryDTO;
import com.emclims.module.test.entity.TestItem;
import com.emclims.module.test.vo.TestItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TestItemServiceImpl 测试项目服务单元测试
 *
 * <p>测试覆盖以下业务方法：
 * <ul>
 *   <li>pageTestItems - 分页查询测试项目</li>
 *   <li>getTestItemDetail - 获取测试项目详情</li>
 *   <li>createTestItem - 新增测试项目</li>
 *   <li>updateTestItem - 更新测试项目</li>
 *   <li>deleteTestItem - 删除测试项目</li>
 *   <li>updateTestItemStatus - 修改测试项目状态</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class TestItemServiceImplTest {

    private TestItemServiceImpl testItemService;

    @BeforeEach
    void setUp() {
        testItemService = new TestItemServiceImpl();

        // 模拟 RequestContextHolder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // ==================== pageTestItems 测试 ====================

    /**
     * 测试正常分页查询测试项目
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestItems() {
        TestItemQueryDTO queryDTO = new TestItemQueryDTO();
        queryDTO.setKeyword("耐压");
        queryDTO.setCategory("emission");
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("耐压测试");
        item.setCategory("emission");
        item.setStatus(1);

        Page<TestItem> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(item));

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestItemVO> result = spy.pageTestItems(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("耐压测试", result.getRecords().get(0).getName());
        assertEquals("发射", result.getRecords().get(0).getCategoryName());
        assertEquals("启用", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试空结果分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestItemsEmpty() {
        TestItemQueryDTO queryDTO = new TestItemQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Page<TestItem> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestItemVO> result = spy.pageTestItems(queryDTO);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    /**
     * 测试按类别筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestItemsByCategory() {
        TestItemQueryDTO queryDTO = new TestItemQueryDTO();
        queryDTO.setCategory("immunity");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        TestItem item = new TestItem();
        item.setId(2L);
        item.setCode("TI-002");
        item.setName("静电放电测试");
        item.setCategory("immunity");
        item.setStatus(1);

        Page<TestItem> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(item));

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestItemVO> result = spy.pageTestItems(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("抗扰度", result.getRecords().get(0).getCategoryName());
    }

    /**
     * 测试无关键字查询返回所有记录
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestItemsNoKeyword() {
        TestItemQueryDTO queryDTO = new TestItemQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        TestItem item1 = new TestItem();
        item1.setId(1L);
        item1.setCode("TI-001");
        item1.setName("测试项目1");
        item1.setCategory("emission");
        item1.setStatus(1);

        TestItem item2 = new TestItem();
        item2.setId(2L);
        item2.setCode("TI-002");
        item2.setName("测试项目2");
        item2.setCategory("immunity");
        item2.setStatus(0);

        Page<TestItem> pageResult = new Page<>(1, 10, 2);
        pageResult.setRecords(List.of(item1, item2));

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestItemVO> result = spy.pageTestItems(queryDTO);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
    }

    // ==================== getTestItemDetail 测试 ====================

    /**
     * 测试正常获取测试项目详情
     */
    @Test
    void testGetTestItemDetail() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("耐压测试");
        item.setStandard("GB/T 17626.3");
        item.setMethod("直接法");
        item.setCategory("emission");
        item.setLimitValue("3V");
        item.setStatus(1);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);

        TestItemVO vo = spy.getTestItemDetail(1L);

        assertNotNull(vo);
        assertEquals("TI-001", vo.getCode());
        assertEquals("耐压测试", vo.getName());
        assertEquals("GB/T 17626.3", vo.getStandard());
        assertEquals("发射", vo.getCategoryName());
        assertEquals("启用", vo.getStatusName());
    }

    /**
     * 测试获取不存在的测试项目详情
     */
    @Test
    void testGetTestItemDetailNotFound() {
        TestItemServiceImpl spy = spy(testItemService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getTestItemDetail(999L));
        assertEquals("测试项目不存在", exception.getMessage());
    }

    /**
     * 测试获取 category 为 null 的测试项目详情
     */
    @Test
    void testGetTestItemDetailNullCategory() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("未知类别项目");
        item.setCategory(null);
        item.setStatus(1);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);

        TestItemVO vo = spy.getTestItemDetail(1L);

        assertNotNull(vo);
        assertEquals("未知类别项目", vo.getName());
        assertEquals("", vo.getCategoryName());
    }

    /**
     * 测试获取 status 为 0 的测试项目详情
     */
    @Test
    void testGetTestItemDetailDisabled() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("禁用项目");
        item.setCategory("emission");
        item.setStatus(0);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);

        TestItemVO vo = spy.getTestItemDetail(1L);

        assertNotNull(vo);
        assertEquals("禁用", vo.getStatusName());
    }

    /**
     * 测试获取 status 为 null 的测试项目详情
     */
    @Test
    void testGetTestItemDetailNullStatus() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("状态未知项目");
        item.setCategory("emission");
        item.setStatus(null);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);

        TestItemVO vo = spy.getTestItemDetail(1L);

        assertNotNull(vo);
        assertEquals("", vo.getStatusName());
    }

    // ==================== createTestItem 测试 ====================

    /**
     * 测试正常创建测试项目
     */
    @Test
    void testCreateTestItem() {
        TestItemDTO dto = new TestItemDTO();
        dto.setCode("TI-003");
        dto.setName("浪涌测试");
        dto.setStandard("GB/T 17626.5");
        dto.setMethod("耦合/去耦网络法");
        dto.setCategory("immunity");
        dto.setLimitValue("1kV");
        dto.setRemark("高压浪涌测试");

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(true).when(spy).save(any(TestItem.class));

        assertDoesNotThrow(() -> spy.createTestItem(dto));
        verify(spy).save(any(TestItem.class));
    }

    /**
     * 测试创建测试项目 - 空备注
     */
    @Test
    void testCreateTestItemWithNullRemark() {
        TestItemDTO dto = new TestItemDTO();
        dto.setCode("TI-004");
        dto.setName("传导骚扰测试");
        dto.setCategory("emission");

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(true).when(spy).save(any(TestItem.class));

        assertDoesNotThrow(() -> spy.createTestItem(dto));
        verify(spy).save(any(TestItem.class));
    }

    /**
     * 测试创建测试项目 - 完整字段
     */
    @Test
    void testCreateTestItemFullFields() {
        TestItemDTO dto = new TestItemDTO();
        dto.setCode("TI-005");
        dto.setName("瞬态脉冲群测试");
        dto.setStandard("GB/T 17626.4");
        dto.setMethod("直接注入法");
        dto.setCategory("immunity");
        dto.setLimitValue("1kV");
        dto.setRemark("信号端口测试");

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(true).when(spy).save(any(TestItem.class));

        assertDoesNotThrow(() -> spy.createTestItem(dto));
        verify(spy).save(any(TestItem.class));
    }

    // ==================== updateTestItem 测试 ====================

    /**
     * 测试正常更新测试项目
     */
    @Test
    void testUpdateTestItem() {
        TestItemDTO dto = new TestItemDTO();
        dto.setId(1L);
        dto.setCode("TI-001");
        dto.setName("更新后的耐压测试");
        dto.setStandard("GB/T 17626.3-202X");
        dto.setCategory("emission");

        TestItem existingItem = new TestItem();
        existingItem.setId(1L);
        existingItem.setCode("TI-001");
        existingItem.setName("耐压测试");
        existingItem.setCategory("emission");
        existingItem.setStatus(1);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(existingItem).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestItem.class));

        assertDoesNotThrow(() -> spy.updateTestItem(dto));
        verify(spy).updateById(any(TestItem.class));
    }

    /**
     * 测试更新不存在的测试项目
     */
    @Test
    void testUpdateTestItemNotFound() {
        TestItemDTO dto = new TestItemDTO();
        dto.setId(999L);
        dto.setCode("TI-999");
        dto.setName("不存在的项目");
        dto.setCategory("emission");

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateTestItem(dto));
        assertEquals("测试项目不存在", exception.getMessage());
        verify(spy, never()).updateById(any(TestItem.class));
    }

    // ==================== deleteTestItem 测试 ====================

    /**
     * 测试正常删除测试项目
     */
    @Test
    void testDeleteTestItem() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("耐压测试");

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestItem(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除不存在的测试项目
     */
    @Test
    void testDeleteTestItemNotFound() {
        TestItemServiceImpl spy = spy(testItemService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteTestItem(999L));
        assertEquals("测试项目不存在", exception.getMessage());
        verify(spy, never()).removeById(anyLong());
    }

    // ==================== updateTestItemStatus 测试 ====================

    /**
     * 测试正常修改测试项目状态（启用 -> 禁用）
     */
    @Test
    void testUpdateTestItemStatus() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("耐压测试");
        item.setStatus(1);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestItem.class));

        assertDoesNotThrow(() -> spy.updateTestItemStatus(1L, 0));
        assertEquals(0, item.getStatus());
        verify(spy).updateById(item);
    }

    /**
     * 测试修改测试项目状态 - 禁用 -> 启用
     */
    @Test
    void testUpdateTestItemStatusFromDisableToEnable() {
        TestItem item = new TestItem();
        item.setId(1L);
        item.setCode("TI-001");
        item.setName("耐压测试");
        item.setStatus(0);

        TestItemServiceImpl spy = spy(testItemService);
        doReturn(item).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestItem.class));

        assertDoesNotThrow(() -> spy.updateTestItemStatus(1L, 1));
        assertEquals(1, item.getStatus());
        verify(spy).updateById(item);
    }

    /**
     * 测试修改不存在的测试项目状态
     */
    @Test
    void testUpdateTestItemStatusNotFound() {
        TestItemServiceImpl spy = spy(testItemService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateTestItemStatus(999L, 1));
        assertEquals("测试项目不存在", exception.getMessage());
        verify(spy, never()).updateById(any(TestItem.class));
    }
}
