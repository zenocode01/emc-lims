package com.emclims.module.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.test.dto.TestPlanDTO;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.mapper.TestPlanMapper;
import com.emclims.module.test.vo.TestPlanVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TestPlanServiceImpl 测试计划服务单元测试
 *
 * <p>测试覆盖以下业务方法：
 * <ul>
 *   <li>pageTestPlans - 分页查询测试计划</li>
 *   <li>getTestPlanDetail - 获取测试计划详情</li>
 *   <li>createTestPlan - 创建测试计划</li>
 *   <li>updateTestPlan - 更新测试计划</li>
 *   <li>deleteTestPlan - 删除测试计划</li>
 *   <li>startTest - 开始测试</li>
 *   <li>completeTest - 完成测试</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class TestPlanServiceImplTest {

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private CustomerMapper customerMapper;

    private TestPlanServiceImpl testPlanService;

    @BeforeEach
    void setUp() {
        // 创建实例后手动注入 mapper（字段为 package-private）
        testPlanService = new TestPlanServiceImpl();
        testPlanService.sampleMapper = sampleMapper;
        testPlanService.customerMapper = customerMapper;

        // 模拟 RequestContextHolder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // ==================== pageTestPlans 测试 ====================

    /**
     * 测试正常分页查询测试计划
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestPlans() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("draft");
        plan.setPlanDate(LocalDate.of(2025, 6, 15));
        plan.setDueDate(LocalDate.of(2025, 6, 20));

        Sample sample = new Sample();
        sample.setId(100L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("电机样品");

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        Page<TestPlan> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(plan));

        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(customer)).when(customerMapper).selectBatchIds(anyList());
                TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestPlanVO> result = spy.pageTestPlans(100L, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("TP-20250101-0001", result.getRecords().get(0).getPlanNo());
        assertEquals("SPL-2025-001", result.getRecords().get(0).getSampleNo());
        assertEquals("电机样品", result.getRecords().get(0).getProductName());
        assertEquals("测试客户", result.getRecords().get(0).getCustomerName());
        assertEquals("草稿", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试空结果分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestPlansEmpty() {
        Page<TestPlan> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestPlanVO> result = spy.pageTestPlans(null, null, 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    /**
     * 测试按样品ID筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestPlansBySampleId() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("testing");

        Sample sample = new Sample();
        sample.setId(100L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("电机样品");

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        Page<TestPlan> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(plan));

        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(customer)).when(customerMapper).selectBatchIds(anyList());
                TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestPlanVO> result = spy.pageTestPlans(100L, "testing", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("测试中", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试按状态筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestPlansByStatus() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("completed");

        Page<TestPlan> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(plan));

        doReturn(Collections.emptyList()).when(sampleMapper).selectBatchIds(anyList());
        doReturn(Collections.emptyList()).when(customerMapper).selectBatchIds(anyList());
                TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestPlanVO> result = spy.pageTestPlans(null, "completed", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("已完成", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试无筛选条件分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestPlansNoFilter() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("draft");

        Page<TestPlan> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(plan));

        doReturn(Collections.emptyList()).when(sampleMapper).selectBatchIds(anyList());
        doReturn(Collections.emptyList()).when(customerMapper).selectBatchIds(anyList());
                TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestPlanVO> result = spy.pageTestPlans(null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    // ==================== getTestPlanDetail 测试 ====================

    /**
     * 测试正常获取测试计划详情
     */
    @Test
    void testGetTestPlanDetail() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("draft");
        plan.setPlanDate(LocalDate.of(2025, 6, 15));
        plan.setRemark("第一次测试");

        Sample sample = new Sample();
        sample.setId(100L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("电机样品");

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        doReturn(sample).when(sampleMapper).selectById(100L);
        doReturn(customer).when(customerMapper).selectById(50L);

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        TestPlanVO vo = spy.getTestPlanDetail(1L);

        assertNotNull(vo);
        assertEquals("TP-20250101-0001", vo.getPlanNo());
        assertEquals("SPL-2025-001", vo.getSampleNo());
        assertEquals("电机样品", vo.getProductName());
        assertEquals("测试客户", vo.getCustomerName());
        assertEquals("草稿", vo.getStatusName());
    }

    /**
     * 测试获取不存在的测试计划
     */
    @Test
    void testGetTestPlanDetailNotFound() {
        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getTestPlanDetail(999L));
        assertEquals("测试计划不存在", exception.getMessage());
    }

    /**
     * 测试获取样品ID或客户ID为 null 的测试计划详情
     */
    @Test
    void testGetTestPlanDetailNullIds() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(null);
        plan.setCustomerId(null);
        plan.setStatus("draft");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        TestPlanVO vo = spy.getTestPlanDetail(1L);

        assertNotNull(vo);
        assertEquals("TP-20250101-0001", vo.getPlanNo());
        assertNull(vo.getSampleNo());
        assertNull(vo.getCustomerName());
    }

    /**
     * 测试获取关联样品不存在的情况
     */
    @Test
    void testGetTestPlanDetailSampleNotFound() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setSampleId(100L);
        plan.setCustomerId(50L);
        plan.setStatus("draft");

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        doReturn(null).when(sampleMapper).selectById(100L);
        doReturn(customer).when(customerMapper).selectById(50L);

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        TestPlanVO vo = spy.getTestPlanDetail(1L);

        assertNotNull(vo);
        assertEquals("测试客户", vo.getCustomerName());
        assertNull(vo.getSampleNo());
        assertNull(vo.getProductName());
    }

    // ==================== createTestPlan 测试 ====================

    /**
     * 测试正常创建测试计划
     */
    @SuppressWarnings("unchecked")
    @Test
    void testCreateTestPlan() {
        TestPlanDTO dto = new TestPlanDTO();
        dto.setSampleId(100L);
        dto.setCustomerId(50L);
        dto.setPlanDate(LocalDate.of(2025, 6, 15));
        dto.setDueDate(LocalDate.of(2025, 6, 20));
        dto.setRemark("新测试计划");

        TestPlan createdPlan = new TestPlan();
        createdPlan.setId(1L);
        createdPlan.setPlanNo("TP-20250101-0001");
        createdPlan.setStatus("draft");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(true).when(spy).save(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.createTestPlan(dto));
        verify(spy).save(any(TestPlan.class));
    }

    /**
     * 测试创建测试计划 - 仅必填字段
     */
    @SuppressWarnings("unchecked")
    @Test
    void testCreateTestPlanMinimalFields() {
        TestPlanDTO dto = new TestPlanDTO();
        dto.setSampleId(100L);

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(true).when(spy).save(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.createTestPlan(dto));
        verify(spy).save(any(TestPlan.class));
    }

    /**
     * 测试创建测试计划 - 包含测试项目配置
     */
    @Test
    void testCreateTestPlanWithTestItems() {
        TestPlanDTO dto = new TestPlanDTO();
        dto.setSampleId(100L);
        dto.setCustomerId(50L);
        dto.setTestItems("[{\"testItemId\":1,\"limitValue\":\"3V\"}]");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(true).when(spy).save(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.createTestPlan(dto));
        verify(spy).save(any(TestPlan.class));
    }

    // ==================== updateTestPlan 测试 ====================

    /**
     * 测试正常更新测试计划
     */
    @Test
    void testUpdateTestPlan() {
        TestPlanDTO dto = new TestPlanDTO();
        dto.setId(1L);
        dto.setSampleId(100L);
        dto.setPlanDate(LocalDate.of(2025, 7, 1));
        dto.setDueDate(LocalDate.of(2025, 7, 10));
        dto.setRemark("更新后的备注");

        TestPlan existingPlan = new TestPlan();
        existingPlan.setId(1L);
        existingPlan.setPlanNo("TP-20250101-0001");
        existingPlan.setSampleId(100L);
        existingPlan.setStatus("draft");
        existingPlan.setPlanDate(LocalDate.of(2025, 6, 15));

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(existingPlan).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.updateTestPlan(dto));
        verify(spy).updateById(any(TestPlan.class));
    }

    /**
     * 测试更新不存在的测试计划
     */
    @Test
    void testUpdateTestPlanNotFound() {
        TestPlanDTO dto = new TestPlanDTO();
        dto.setId(999L);
        dto.setSampleId(100L);
        dto.setPlanDate(LocalDate.of(2025, 7, 1));

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateTestPlan(dto));
        assertEquals("测试计划不存在", exception.getMessage());
        verify(spy, never()).updateById(any(TestPlan.class));
    }

    // ==================== deleteTestPlan 测试 ====================

    /**
     * 测试正常删除测试计划
     */
    @Test
    void testDeleteTestPlan() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestPlan(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除不存在的测试计划
     */
    @Test
    void testDeleteTestPlanNotFound() {
        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteTestPlan(999L));
        assertEquals("测试计划不存在", exception.getMessage());
        verify(spy, never()).removeById(anyLong());
    }

    /**
     * 测试删除草稿状态的测试计划
     */
    @Test
    void testDeleteTestPlanDraft() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("draft");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestPlan(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除测试中的测试计划
     */
    @Test
    void testDeleteTestPlanTesting() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("testing");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestPlan(1L));
        verify(spy).removeById(1L);
    }

    // ==================== startTest 测试 ====================

    /**
     * 测试正常开始测试（草稿状态）
     */
    @Test
    void testStartTest() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("draft");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.startTest(1L));
        assertEquals("testing", plan.getStatus());
        verify(spy).updateById(plan);
    }

    /**
     * 测试非草稿状态不能开始测试
     */
    @Test
    void testStartTestWrongStatus() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("testing");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.startTest(1L));
        assertEquals("只有草稿状态的测试计划可以开始测试", exception.getMessage());
        assertEquals("testing", plan.getStatus()); // 状态不应改变
        verify(spy, never()).updateById(any(TestPlan.class));
    }

    /**
     * 测试开始不存在的测试计划
     */
    @Test
    void testStartTestNotFound() {
        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.startTest(999L));
        assertEquals("测试计划不存在", exception.getMessage());
    }

    /**
     * 测试已完成状态的测试计划不能开始
     */
    @Test
    void testStartTestCompleted() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("completed");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.startTest(1L));
        assertEquals("只有草稿状态的测试计划可以开始测试", exception.getMessage());
    }

    /**
     * 测试已取消状态的测试计划不能开始
     */
    @Test
    void testStartTestCancelled() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("cancelled");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        assertThrows(BusinessException.class, () -> spy.startTest(1L));
    }

    // ==================== completeTest 测试 ====================

    /**
     * 测试正常完成测试（测试中状态）
     */
    @Test
    void testCompleteTest() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("testing");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestPlan.class));

        assertDoesNotThrow(() -> spy.completeTest(1L));
        assertEquals("completed", plan.getStatus());
        verify(spy).updateById(plan);
    }

    /**
     * 测试非测试中状态不能完成测试
     */
    @Test
    void testCompleteTestWrongStatus() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("draft");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.completeTest(1L));
        assertEquals("只有测试中的测试计划可以完成", exception.getMessage());
        assertEquals("draft", plan.getStatus()); // 状态不应改变
        verify(spy, never()).updateById(any(TestPlan.class));
    }

    /**
     * 测试完成不存在的测试计划
     */
    @Test
    void testCompleteTestNotFound() {
        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.completeTest(999L));
        assertEquals("测试计划不存在", exception.getMessage());
    }

    /**
     * 测试已完成状态不能再次完成
     */
    @Test
    void testCompleteTestAlreadyCompleted() {
        TestPlan plan = new TestPlan();
        plan.setId(1L);
        plan.setPlanNo("TP-20250101-0001");
        plan.setStatus("completed");

        TestPlanServiceImpl spy = spy(testPlanService);
        doReturn(plan).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.completeTest(1L));
        assertEquals("只有测试中的测试计划可以完成", exception.getMessage());
    }
}
