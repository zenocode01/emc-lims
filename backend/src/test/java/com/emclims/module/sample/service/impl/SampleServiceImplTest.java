package com.emclims.module.sample.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.sample.dto.SampleDTO;
import com.emclims.module.sample.dto.SampleQueryDTO;
import com.emclims.module.sample.dto.SampleStatusDTO;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.entity.SampleLog;
import com.emclims.module.sample.enums.SampleStatusEnum;
import com.emclims.module.sample.mapper.SampleLogMapper;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SampleServiceImpl 样品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SampleServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SampleLogMapper sampleLogMapper;

    private SampleServiceImpl sampleService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        sampleService = new SampleServiceImpl(customerMapper, userMapper, sampleLogMapper);
    }

    // === pageSamples 测试 ===

    @Test
    void testPageSamples() {
        SampleQueryDTO queryDTO = new SampleQueryDTO();
        queryDTO.setKeyword("样品1");
        queryDTO.setStatus("testing");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Sample sample = new Sample();
        sample.setId(1L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("样品1");
        sample.setStatus("testing");
        sample.setCustomerId(100L);
        sample.setTesterId(10L);

        Page<Sample> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(sample));

        Customer customer = new Customer();
        customer.setName("测试客户");
        SysUser user = new SysUser();
        user.setNickname("测试员");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));
        doReturn(customer).when(customerMapper).selectById(100L);
        doReturn(user).when(userMapper).selectById(10L);

        Page<SampleVO> result = spy.pageSamples(queryDTO);
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("样品1", result.getRecords().get(0).getProductName());
        assertEquals("测试客户", result.getRecords().get(0).getCustomerName());
        assertEquals("测试员", result.getRecords().get(0).getTesterName());
        verify(spy).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageSamplesEmpty() {
        SampleQueryDTO queryDTO = new SampleQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Page<Sample> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(List.of());

        SampleServiceImpl spy = spy(sampleService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<SampleVO> result = spy.pageSamples(queryDTO);
        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
    }

    // === getSampleDetail 测试 ===

    @Test
    void testGetSampleDetail() {
        Sample sample = new Sample();
        sample.setId(1L);
        sample.setSampleNo("SPL-2025-001");
        sample.setProductName("样品1");
        sample.setStatus("testing");
        sample.setCustomerId(100L);
        sample.setTesterId(10L);

        Customer customer = new Customer();
        customer.setName("测试客户");
        SysUser user = new SysUser();
        user.setNickname("测试员");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(sample).when(spy).getById(1L);
        doReturn(customer).when(customerMapper).selectById(100L);
        doReturn(user).when(userMapper).selectById(10L);

        SampleVO vo = spy.getSampleDetail(1L);
        assertNotNull(vo);
        assertEquals("样品1", vo.getProductName());
        assertEquals("测试客户", vo.getCustomerName());
        assertEquals("测试员", vo.getTesterName());
    }

    @Test
    void testGetSampleDetailNotFound() {
        SampleServiceImpl spy = spy(sampleService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.getSampleDetail(999L));
    }

    // === receiveSample 测试 ===

    @Test
    void testReceiveSample() {
        SampleDTO dto = new SampleDTO();
        dto.setCustomerId(100L);
        dto.setProductName("新样品");
        dto.setReceiveDate(LocalDate.of(2025, 6, 15));
        dto.setSampleCount(5);

        SampleServiceImpl spy = spy(sampleService);
        doReturn(true).when(spy).save(any(Sample.class));

        assertDoesNotThrow(() -> spy.receiveSample(dto));
        verify(spy).save(any(Sample.class));
    }

    @Test
    void testReceiveSampleWithNullCustomerId() {
        SampleDTO dto = new SampleDTO();
        dto.setCustomerId(null);
        dto.setProductName("样品");
        dto.setReceiveDate(LocalDate.of(2025, 6, 15));

        SampleServiceImpl spy = spy(sampleService);
        doReturn(true).when(spy).save(any(Sample.class));

        assertDoesNotThrow(() -> spy.receiveSample(dto));
        verify(spy).save(any(Sample.class));
    }

    // === updateSample 测试 ===

    @Test
    void testUpdateSample() {
        SampleDTO dto = new SampleDTO();
        dto.setId(1L);
        dto.setProductName("更新后的样品名称");

        Sample existingSample = new Sample();
        existingSample.setId(1L);
        existingSample.setProductName("旧名称");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(existingSample).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Sample.class));

        assertDoesNotThrow(() -> spy.updateSample(dto));
        verify(spy).updateById(any(Sample.class));
    }

    @Test
    void testUpdateSampleNotFound() {
        SampleDTO dto = new SampleDTO();
        dto.setId(999L);
        dto.setProductName("新名称");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateSample(dto));
    }

    // === deleteSamples 测试 ===

    @Test
    void testDeleteSamples() {
        SampleServiceImpl spy = spy(sampleService);
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteSamples(Arrays.asList(1L, 2L, 3L)));
        verify(spy).removeByIds(Arrays.asList(1L, 2L, 3L));
    }

    // === changeStatus 测试 ===

    @Test
    void testChangeStatus() {
        SampleStatusDTO dto = new SampleStatusDTO();
        dto.setSampleId(1L);
        dto.setToStatus("testing");
        dto.setRemark("开始测试");

        Sample existingSample = new Sample();
        existingSample.setId(1L);
        existingSample.setStatus("received");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(existingSample).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Sample.class));

        assertDoesNotThrow(() -> spy.changeStatus(dto));
        verify(spy).updateById(any(Sample.class));
    }

    @Test
    void testChangeStatusNotFound() {
        SampleStatusDTO dto = new SampleStatusDTO();
        dto.setSampleId(999L);
        dto.setToStatus("testing");

        SampleServiceImpl spy = spy(sampleService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.changeStatus(dto));
    }

    // === getSampleLogs 测试 ===

    @Test
    void testGetSampleLogs() {
        SampleLog log1 = new SampleLog();
        log1.setId(1L);
        log1.setSampleId(1L);
        log1.setFromStatus("received");
        log1.setToStatus("testing");
        log1.setRemark("开始测试");
        log1.setOperator(1L);
        log1.setOperateTime(LocalDateTime.now());

        SampleLog log2 = new SampleLog();
        log2.setId(2L);
        log2.setSampleId(1L);
        log2.setFromStatus("pending");
        log2.setToStatus("received");
        log2.setRemark("已收样");
        log2.setOperator(1L);
        log2.setOperateTime(LocalDateTime.now().minusDays(1));

        doReturn(List.of(log1, log2)).when(sampleLogMapper).selectList(any(LambdaQueryWrapper.class));

        List<SampleLogVO> result = sampleService.getSampleLogs(1L);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("received", result.get(0).getFromStatus());
        assertEquals("testing", result.get(0).getToStatus());
        verify(sampleLogMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetSampleLogsEmpty() {
        doReturn(Collections.emptyList()).when(sampleLogMapper).selectList(any(LambdaQueryWrapper.class));

        List<SampleLogVO> result = sampleService.getSampleLogs(1L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
