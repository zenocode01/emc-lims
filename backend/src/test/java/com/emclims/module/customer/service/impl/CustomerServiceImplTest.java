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
}
