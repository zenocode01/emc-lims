package com.emclims.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.dto.CustomerDTO;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.vo.CustomerExportVO;
import com.emclims.module.customer.vo.CustomerVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CustomerServiceImpl 客户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    // === pageCustomers 测试 ===

    @Test
    void testPageCustomers() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("测试");
        queryDTO.setType(1);
        queryDTO.setIndustry("医药");
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("测试客户");
        customer.setContact("张三");
        customer.setPhone("13800138000");
        customer.setType(1);
        customer.setIndustry("医药");
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("测试客户", result.getRecords().get(0).getName());
        assertEquals("企业", result.getRecords().get(0).getTypeName());
        verify(spy).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageCustomersEmpty() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Page<Customer> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(List.of());

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);
        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
    }

    @Test
    void testPageCustomersWithPersonalType() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setType(2);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("个人客户");
        customer.setType(2);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);
        assertNotNull(result);
        assertEquals("个人", result.getRecords().get(0).getTypeName());
    }

    // === getCustomerDetail 测试 ===

    @Test
    void testGetCustomerDetail() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("测试客户");
        customer.setContact("张三");
        customer.setPhone("13800138000");
        customer.setType(1);
        customer.setIndustry("医药");
        customer.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);

        CustomerVO vo = spy.getCustomerDetail(1L);
        assertNotNull(vo);
        assertEquals("测试客户", vo.getName());
        assertEquals("企业", vo.getTypeName());
    }

    @Test
    void testGetCustomerDetailNotFound() {
        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.getCustomerDetail(999L));
    }

    // === createCustomer 测试 ===

    @Test
    void testCreateCustomer() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("新客户");
        dto.setContact("李四");
        dto.setPhone("13900139000");
        dto.setType(1);
        dto.setIndustry("医疗");
        dto.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(Customer.class));

        assertDoesNotThrow(() -> spy.createCustomer(dto));
        verify(spy).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerDuplicateName() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("已有客户");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> spy.createCustomer(dto));
        assertEquals("客户名称已存在", exception.getMessage());
        verify(spy, never()).save(any());
    }

    // === updateCustomer 测试 ===

    @Test
    void testUpdateCustomer() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setName("新名称");
        dto.setContact("王五");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("旧名称");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(existingCustomer).when(spy).getById(1L);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).updateById(any(Customer.class));

        assertDoesNotThrow(() -> spy.updateCustomer(dto));
        verify(spy).updateById(any(Customer.class));
    }

    @Test
    void testUpdateCustomerNotFound() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(999L);
        dto.setName("新名称");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateCustomer(dto));
    }

    @Test
    void testUpdateCustomerNameUsedByOther() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setName("其他客户");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("当前客户");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(existingCustomer).when(spy).getById(1L);
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.updateCustomer(dto));
    }

    // === deleteCustomers 测试 ===

    @Test
    void testDeleteCustomers() {
        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteCustomers(Arrays.asList(1L, 2L, 3L)));
        verify(spy).removeByIds(Arrays.asList(1L, 2L, 3L));
    }

    // === updateStatus 测试 ===

    @Test
    void testUpdateStatus() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Customer.class));

        assertDoesNotThrow(() -> spy.updateStatus(1L, 0));
        assertEquals(0, customer.getStatus());
        verify(spy).updateById(customer);
    }

    @Test
    void testUpdateStatusNotFound() {
        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateStatus(999L, 0));
    }

    // === exportCustomers 测试 ===
    // exportCustomers 使用 list() 底层调用 getBaseMapper().selectList()
    // 在单元测试中 getBaseMapper() 返回 null，所以此方法依赖 Spring 上下文或模拟 mapper
    @Test
    @SuppressWarnings("unchecked")
    void testExportCustomersWithSpy() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(100);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn((List) Collections.emptyList()).when(spy).<List>list(any(LambdaQueryWrapper.class));

        // exportCustomers 调用 list() 返回空列表，然后尝试遍历
        // 在空列表情况下不会触发 NPE
        assertDoesNotThrow(() -> spy.exportCustomers(queryDTO));
    }

    // === 空关键字搜索测试 ===

    /**
     * 测试空关键字搜索 - 返回所有客户
     */
    @Test
    void testPageCustomersEmptyKeyword() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword(null);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("无关键字客户");
        customer.setType(1);
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    /**
     * 测试空字符串关键字搜索
     */
    @Test
    void testPageCustomersEmptyStringKeyword() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("空关键字客户");
        customer.setType(1);
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    /**
     * 测试按联系人关键字搜索
     */
    @Test
    void testPageCustomersByContact() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("李");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("某客户");
        customer.setContact("李四");
        customer.setType(1);
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("李四", result.getRecords().get(0).getContact());
    }

    /**
     * 测试按电话关键字搜索
     */
    @Test
    void testPageCustomersByPhone() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("138");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("电话客户");
        customer.setPhone("13800138000");
        customer.setType(2);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("个人", result.getRecords().get(0).getTypeName());
    }

    // === 边界条件测试 ===

    /**
     * 测试获取 type 为 null 的客户详情
     */
    @Test
    void testGetCustomerDetailNullType() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("未知类型客户");
        customer.setType(null);
        customer.setIndustry("其他");
        customer.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);

        CustomerVO vo = spy.getCustomerDetail(1L);

        assertNotNull(vo);
        assertEquals("未知类型客户", vo.getName());
        // type 为 null 时，typeName 应为 "个人"（因为 type != 1）
        assertEquals("个人", vo.getTypeName());
    }

    /**
     * 测试获取 status 为 null 的客户详情
     */
    @Test
    void testGetCustomerDetailNullStatus() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("状态未知客户");
        customer.setType(1);
        customer.setStatus(null);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);

        CustomerVO vo = spy.getCustomerDetail(1L);

        assertNotNull(vo);
        assertEquals("状态未知客户", vo.getName());
        assertEquals("企业", vo.getTypeName());
    }

    /**
     * 测试创建客户 - 仅必填字段
     */
    @Test
    void testCreateCustomerMinimalFields() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("最少信息客户");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(Customer.class));

        assertDoesNotThrow(() -> spy.createCustomer(dto));
        verify(spy).save(any(Customer.class));
    }

    /**
     * 测试创建客户 - 所有字段
     */
    @Test
    void testCreateCustomerFullFields() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("完整客户");
        dto.setType(1);
        dto.setIndustry("医疗");
        dto.setAddress("北京市朝阳区");
        dto.setPhone("13900139000");
        dto.setEmail("customer@example.com");
        dto.setContact("联系人");
        dto.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(Customer.class));

        assertDoesNotThrow(() -> spy.createCustomer(dto));
        verify(spy).save(any(Customer.class));
    }

    /**
     * 测试更新客户 - 只更新部分字段
     */
    @Test
    void testUpdateCustomerPartialFields() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setPhone("13900139000");
        dto.setEmail("new@example.com");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("原名称");
        existingCustomer.setPhone("13800138000");

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(existingCustomer).when(spy).getById(1L);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).updateById(any(Customer.class));

        assertDoesNotThrow(() -> spy.updateCustomer(dto));
        verify(spy).updateById(any(Customer.class));
    }

    /**
     * 测试按行业筛选查询
     */
    @Test
    void testPageCustomersByIndustry() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setIndustry("医疗");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("医疗客户");
        customer.setType(1);
        customer.setIndustry("医疗");
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("医疗客户", result.getRecords().get(0).getName());
        assertEquals("医疗", result.getRecords().get(0).getIndustry());
    }

    /**
     * 测试组合条件查询
     */
    @Test
    void testPageCustomersCombinedConditions() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("药");
        queryDTO.setType(1);
        queryDTO.setIndustry("医药");
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("医药科技公司");
        customer.setContact("王经理");
        customer.setType(1);
        customer.setIndustry("医药");
        customer.setStatus(1);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("医药科技公司", result.getRecords().get(0).getName());
        assertEquals("企业", result.getRecords().get(0).getTypeName());
    }

    /**
     * 测试禁用状态客户
     */
    @Test
    void testPageCustomersDisabled() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setStatus(0);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("禁用客户");
        customer.setType(1);
        customer.setStatus(0);

        Page<Customer> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(customer));

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<CustomerVO> result = spy.pageCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("禁用客户", result.getRecords().get(0).getName());
        assertEquals(0, result.getRecords().get(0).getStatus());
    }

    /**
     * 测试删除单个客户
     */
    @Test
    void testDeleteCustomersSingle() {
        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteCustomers(Arrays.asList(1L)));
        verify(spy).removeByIds(Arrays.asList(1L));
    }

    /**
     * 测试导出客户 - 带数据
     */
    @Test
    @SuppressWarnings("unchecked")
    void testExportCustomersWithData() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setStatus(1);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(100);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("导出客户");
        customer.setType(1);
        customer.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn((List) List.of(customer)).when(spy).<List>list(any(LambdaQueryWrapper.class));

        List<CustomerExportVO> result = spy.exportCustomers(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("导出客户", result.get(0).getName());
        assertEquals("企业", result.get(0).getTypeName());
        assertEquals("启用", result.get(0).getStatusName());
    }

    /**
     * 测试更新状态 - 从启用到禁用
     */
    @Test
    void testUpdateStatusFromEnableToDisable() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setStatus(1);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Customer.class));

        assertDoesNotThrow(() -> spy.updateStatus(1L, 0));
        assertEquals(0, customer.getStatus());
        verify(spy).updateById(customer);
    }

    /**
     * 测试更新状态 - 从禁用到启用
     */
    @Test
    void testUpdateStatusFromDisableToEnable() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setStatus(0);

        CustomerServiceImpl spy = spy(new CustomerServiceImpl());
        doReturn(customer).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Customer.class));

        assertDoesNotThrow(() -> spy.updateStatus(1L, 1));
        assertEquals(1, customer.getStatus());
        verify(spy).updateById(customer);
    }
}
