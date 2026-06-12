package com.emclims.module.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.test.dto.TestRecordDTO;
import com.emclims.module.test.dto.TestRecordQueryDTO;
import com.emclims.module.test.entity.TestItem;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.entity.TestRecord;
import com.emclims.module.test.mapper.TestItemMapper;
import com.emclims.module.test.mapper.TestPlanMapper;
import com.emclims.module.test.vo.TestRecordVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TestRecordServiceImpl 测试记录服务单元测试
 *
 * <p>测试覆盖以下业务方法：
 * <ul>
 *   <li>pageTestRecords - 分页查询测试记录</li>
 *   <li>getTestRecordDetail - 获取测试记录详情</li>
 *   <li>createTestRecord - 新增测试记录</li>
 *   <li>updateTestRecord - 更新测试记录</li>
 *   <li>deleteTestRecord - 删除测试记录</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class TestRecordServiceImplTest {

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private TestPlanMapper testPlanMapper;

    @Mock
    private TestItemMapper testItemMapper;

    @Mock
    private SysUserMapper userMapper;

    private TestRecordServiceImpl testRecordService;

    @BeforeEach
    void setUp() {
        testRecordService = new TestRecordServiceImpl();
        testRecordService.sampleMapper = sampleMapper;
        testRecordService.testPlanMapper = testPlanMapper;
        testRecordService.testItemMapper = testItemMapper;
        testRecordService.userMapper = userMapper;

        // 模拟 RequestContextHolder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // ==================== pageTestRecords 测试 ====================

    /**
     * 测试正常分页查询测试记录
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecords() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");
        record.setMeasurementValue("2.5V");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Sample sample = new Sample();
        sample.setId(50L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("电机样品");

        TestItem testItem = new TestItem();
        testItem.setId(200L);
        testItem.setCode("TI-001");
        testItem.setName("耐压测试");
        testItem.setCategory("emission");

        SysUser user = new SysUser();
        user.setId(10L);
        user.setNickname("张三");

        Page<TestRecord> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(record));

        doReturn(List.of(testPlan)).when(testPlanMapper).selectBatchIds(anyList());
        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(testItem)).when(testItemMapper).selectBatchIds(anyList());
        doReturn(List.of(user)).when(userMapper).selectBatchIds(anyList());

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(new TestRecordQueryDTO());

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        TestRecordVO vo = result.getRecords().get(0);
        assertEquals("TP-20250101-0001", vo.getPlanNo());
        assertEquals("SPL-2025-001", vo.getSampleNo());
        assertEquals("电机样品", vo.getProductName());
        assertEquals("TI-001", vo.getTestItemCode());
        assertEquals("耐压测试", vo.getTestItemName());
        assertEquals("张三", vo.getTesterName());
        assertEquals("通过", vo.getResultName());
    }

    /**
     * 测试空结果分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecordsEmpty() {
        Page<TestRecord> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(new TestRecordQueryDTO());

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    /**
     * 测试按测试计划ID筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecordsByTestPlanId() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("fail");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Sample sample = new Sample();
        sample.setId(50L);
        sample.setSampleNo("SPL-2025-001");

        TestItem testItem = new TestItem();
        testItem.setId(200L);
        testItem.setCode("TI-001");
        testItem.setName("耐压测试");

        SysUser user = new SysUser();
        user.setId(10L);
        user.setNickname("张三");

        Page<TestRecord> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(record));

        doReturn(List.of(testPlan)).when(testPlanMapper).selectBatchIds(anyList());
        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(testItem)).when(testItemMapper).selectBatchIds(anyList());
        doReturn(List.of(user)).when(userMapper).selectBatchIds(anyList());

        TestRecordQueryDTO queryDTO = new TestRecordQueryDTO();
        queryDTO.setTestPlanId(100L);

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("不通过", result.getRecords().get(0).getResultName());
    }

    /**
     * 测试按结果筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecordsByResult() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("na");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Page<TestRecord> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(record));

        doReturn(List.of(testPlan)).when(testPlanMapper).selectBatchIds(anyList());
        doReturn(Collections.emptyList()).when(sampleMapper).selectBatchIds(anyList());
        doReturn(Collections.emptyList()).when(testItemMapper).selectBatchIds(anyList());
        doReturn(Collections.emptyList()).when(userMapper).selectBatchIds(anyList());

        TestRecordQueryDTO queryDTO = new TestRecordQueryDTO();
        queryDTO.setResult("na");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("不适用", result.getRecords().get(0).getResultName());
    }

    /**
     * 测试带日期范围筛选的分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecordsByDateRange() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Sample sample = new Sample();
        sample.setId(50L);
        sample.setSampleNo("SPL-2025-001");

        TestItem testItem = new TestItem();
        testItem.setId(200L);
        testItem.setCode("TI-001");

        SysUser user = new SysUser();
        user.setId(10L);
        user.setNickname("测试员");

        Page<TestRecord> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(record));

        doReturn(List.of(testPlan)).when(testPlanMapper).selectBatchIds(anyList());
        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(testItem)).when(testItemMapper).selectBatchIds(anyList());
        doReturn(List.of(user)).when(userMapper).selectBatchIds(anyList());

        TestRecordQueryDTO queryDTO = new TestRecordQueryDTO();
        queryDTO.setStartDate(LocalDateTime.of(2025, 6, 1, 0, 0));
        queryDTO.setEndDate(LocalDateTime.of(2025, 6, 30, 23, 59));

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    /**
     * 测试按测试人员ID筛选分页查询
     */
    @SuppressWarnings("unchecked")
    @Test
    void testPageTestRecordsByTesterId() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Sample sample = new Sample();
        sample.setId(50L);
        sample.setSampleNo("SPL-2025-001");

        TestItem testItem = new TestItem();
        testItem.setId(200L);
        testItem.setCode("TI-001");

        SysUser user = new SysUser();
        user.setId(10L);
        user.setNickname("张三");

        Page<TestRecord> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(record));

        doReturn(List.of(testPlan)).when(testPlanMapper).selectBatchIds(anyList());
        doReturn(List.of(sample)).when(sampleMapper).selectBatchIds(anyList());
        doReturn(List.of(testItem)).when(testItemMapper).selectBatchIds(anyList());
        doReturn(List.of(user)).when(userMapper).selectBatchIds(anyList());

        TestRecordQueryDTO queryDTO = new TestRecordQueryDTO();
        queryDTO.setTesterId(10L);

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<TestRecordVO> result = spy.pageTestRecords(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("张三", result.getRecords().get(0).getTesterName());
    }

    // ==================== getTestRecordDetail 测试 ====================

    /**
     * 测试正常获取测试记录详情
     */
    @Test
    void testGetTestRecordDetail() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(10L);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");
        record.setMeasurementValue("2.5V");
        record.setLimitValue("3.0V");
        record.setMargin(new BigDecimal("0.5"));

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        Sample sample = new Sample();
        sample.setId(50L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("电机样品");

        TestItem testItem = new TestItem();
        testItem.setId(200L);
        testItem.setCode("TI-001");
        testItem.setName("耐压测试");
        testItem.setCategory("emission");

        SysUser user = new SysUser();
        user.setId(10L);
        user.setNickname("张三");

        doReturn(testPlan).when(testPlanMapper).selectById(100L);
        doReturn(sample).when(sampleMapper).selectById(50L);
        doReturn(testItem).when(testItemMapper).selectById(200L);
        doReturn(user).when(userMapper).selectById(10L);

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);

        TestRecordVO vo = spy.getTestRecordDetail(1L);

        assertNotNull(vo);
        assertEquals("TP-20250101-0001", vo.getPlanNo());
        assertEquals("SPL-2025-001", vo.getSampleNo());
        assertEquals("电机样品", vo.getProductName());
        assertEquals("TI-001", vo.getTestItemCode());
        assertEquals("耐压测试", vo.getTestItemName());
        assertEquals("张三", vo.getTesterName());
        assertEquals("通过", vo.getResultName());
        assertEquals("2.5V", vo.getMeasurementValue());
        assertEquals("3.0V", vo.getLimitValue());
    }

    /**
     * 测试获取不存在的测试记录
     */
    @Test
    void testGetTestRecordDetailNotFound() {
        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getTestRecordDetail(999L));
        assertEquals("测试记录不存在", exception.getMessage());
    }

    /**
     * 测试获取关联数据为 null 的测试记录详情
     */
    @Test
    void testGetTestRecordDetailWithNullRelations() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(null);
        record.setTestItemId(null);
        record.setTesterId(null);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);

        TestRecordVO vo = spy.getTestRecordDetail(1L);

        assertNotNull(vo);
        assertNull(vo.getPlanNo());
        assertNull(vo.getSampleNo());
        assertNull(vo.getProductName());
        assertNull(vo.getTestItemCode());
        assertNull(vo.getTestItemName());
        assertNull(vo.getTesterName());
        assertEquals("通过", vo.getResultName());
    }

    /**
     * 测试获取部分关联数据为 null 的测试记录详情
     */
    @Test
    void testGetTestRecordDetailPartialNullRelations() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setTestItemId(200L);
        record.setTesterId(null);
        record.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        record.setResult("pass");

        TestPlan testPlan = new TestPlan();
        testPlan.setId(100L);
        testPlan.setPlanNo("TP-20250101-0001");
        testPlan.setSampleId(50L);

        doReturn(testPlan).when(testPlanMapper).selectById(100L);
        doReturn(null).when(sampleMapper).selectById(50L);
        doReturn(null).when(testItemMapper).selectById(200L);

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);

        TestRecordVO vo = spy.getTestRecordDetail(1L);

        assertNotNull(vo);
        assertEquals("TP-20250101-0001", vo.getPlanNo());
        assertNull(vo.getSampleNo());
        assertNull(vo.getProductName());
        assertNull(vo.getTestItemCode());
        assertNull(vo.getTestItemName());
        assertNull(vo.getTesterName());
    }

    // ==================== createTestRecord 测试 ====================

    /**
     * 测试正常新增测试记录
     */
    @Test
    void testCreateTestRecord() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setTestPlanId(100L);
        dto.setTestItemId(200L);
        dto.setTesterId(10L);
        dto.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        dto.setResult("pass");
        dto.setMeasurementValue("2.5V");
        dto.setLimitValue("3.0V");
        dto.setMargin(new BigDecimal("0.5"));
        dto.setTestCondition("标准条件");
        dto.setRemarks("测试通过");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(true).when(spy).save(any(TestRecord.class));

        assertDoesNotThrow(() -> spy.createTestRecord(dto));
        verify(spy).save(any(TestRecord.class));
    }

    /**
     * 测试新增测试记录 - 最小必填字段
     */
    @Test
    void testCreateTestRecordMinimalFields() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setTestPlanId(100L);
        dto.setTestItemId(200L);
        dto.setTesterId(10L);
        dto.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        dto.setResult("pass");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(true).when(spy).save(any(TestRecord.class));

        assertDoesNotThrow(() -> spy.createTestRecord(dto));
        verify(spy).save(any(TestRecord.class));
    }

    /**
     * 测试新增测试记录 - 不通过结果
     */
    @Test
    void testCreateTestRecordFailResult() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setTestPlanId(100L);
        dto.setTestItemId(200L);
        dto.setTesterId(10L);
        dto.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        dto.setResult("fail");
        dto.setMeasurementValue("5.0V");
        dto.setLimitValue("3.0V");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(true).when(spy).save(any(TestRecord.class));

        assertDoesNotThrow(() -> spy.createTestRecord(dto));
        verify(spy).save(any(TestRecord.class));
    }

    /**
     * 测试新增测试记录 - 不适用结果
     */
    @Test
    void testCreateTestRecordNaResult() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setTestPlanId(100L);
        dto.setTestItemId(200L);
        dto.setTesterId(10L);
        dto.setTestDate(LocalDateTime.of(2025, 6, 15, 10, 30));
        dto.setResult("na");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(true).when(spy).save(any(TestRecord.class));

        assertDoesNotThrow(() -> spy.createTestRecord(dto));
        verify(spy).save(any(TestRecord.class));
    }

    // ==================== updateTestRecord 测试 ====================

    /**
     * 测试正常更新测试记录
     */
    @Test
    void testUpdateTestRecord() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setId(1L);
        dto.setResult("pass");
        dto.setMeasurementValue("2.3V");
        dto.setLimitValue("3.0V");
        dto.setMargin(new BigDecimal("0.7"));

        TestRecord existingRecord = new TestRecord();
        existingRecord.setId(1L);
        existingRecord.setResult("fail");
        existingRecord.setMeasurementValue("2.5V");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(existingRecord).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(TestRecord.class));

        assertDoesNotThrow(() -> spy.updateTestRecord(dto));
        verify(spy).updateById(any(TestRecord.class));
    }

    /**
     * 测试更新不存在的测试记录
     */
    @Test
    void testUpdateTestRecordNotFound() {
        TestRecordDTO dto = new TestRecordDTO();
        dto.setId(999L);
        dto.setResult("pass");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateTestRecord(dto));
        assertEquals("测试记录不存在", exception.getMessage());
        verify(spy, never()).updateById(any(TestRecord.class));
    }

    // ==================== deleteTestRecord 测试 ====================

    /**
     * 测试正常删除测试记录
     */
    @Test
    void testDeleteTestRecord() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setResult("pass");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestRecord(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除不存在的测试记录
     */
    @Test
    void testDeleteTestRecordNotFound() {
        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteTestRecord(999L));
        assertEquals("测试记录不存在", exception.getMessage());
        verify(spy, never()).removeById(anyLong());
    }

    /**
     * 测试删除结果值为 null 的测试记录
     */
    @Test
    void testDeleteTestRecordWithNullResult() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setResult(null);

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestRecord(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除 pass 结果的测试记录
     */
    @Test
    void testDeleteTestRecordPassResult() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setResult("pass");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestRecord(1L));
        verify(spy).removeById(1L);
    }

    /**
     * 测试删除 fail 结果的测试记录
     */
    @Test
    void testDeleteTestRecordFailResult() {
        TestRecord record = new TestRecord();
        record.setId(1L);
        record.setTestPlanId(100L);
        record.setResult("fail");

        TestRecordServiceImpl spy = spy(testRecordService);
        doReturn(record).when(spy).getById(1L);
        doReturn(true).when(spy).removeById(anyLong());

        assertDoesNotThrow(() -> spy.deleteTestRecord(1L));
        verify(spy).removeById(1L);
    }
}
