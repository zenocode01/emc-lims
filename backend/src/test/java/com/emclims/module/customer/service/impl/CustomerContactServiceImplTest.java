package com.emclims.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.dto.CustomerContactDTO;
import com.emclims.module.customer.entity.CustomerContact;
import com.emclims.module.customer.vo.CustomerContactVO;
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
 * CustomerContactServiceImpl 联系人服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CustomerContactServiceImplTest {

    private CustomerContactServiceImpl contactService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        contactService = new CustomerContactServiceImpl();
    }

    // === listByCustomerId 测试 ===

    @Test
    @SuppressWarnings("unchecked")
    void testListByCustomerId() {
        CustomerContact contact1 = new CustomerContact();
        contact1.setId(1L);
        contact1.setCustomerId(100L);
        contact1.setName("张三");
        contact1.setPhone("13800138000");
        contact1.setEmail("zhangsan@example.com");
        contact1.setPosition("经理");
        contact1.setIsPrimary(1);

        CustomerContact contact2 = new CustomerContact();
        contact2.setId(2L);
        contact2.setCustomerId(100L);
        contact2.setName("李四");
        contact2.setPhone("13900139000");
        contact2.setEmail("lisi@example.com");
        contact2.setPosition("助理");
        contact2.setIsPrimary(0);

        CustomerContactServiceImpl spy = spy(contactService);
        doReturn((List) List.of(contact1, contact2)).when(spy).<List>list(any(LambdaQueryWrapper.class));

        List<CustomerContactVO> result = spy.listByCustomerId(100L);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
        verify(spy).list(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListByCustomerIdEmpty() {
        CustomerContactServiceImpl spy = spy(contactService);
        doReturn((List) Collections.emptyList()).when(spy).<List>list(any(LambdaQueryWrapper.class));

        List<CustomerContactVO> result = spy.listByCustomerId(999L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // === createContact 测试 ===

    @Test
    void testCreateContact() {
        CustomerContactDTO dto = new CustomerContactDTO();
        dto.setCustomerId(100L);
        dto.setName("张三");
        dto.setPhone("13800138000");
        dto.setEmail("zhangsan@example.com");
        dto.setPosition("经理");
        dto.setIsPrimary(0);

        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(true).when(spy).save(any(CustomerContact.class));

        assertDoesNotThrow(() -> spy.createContact(dto));
        verify(spy).save(any(CustomerContact.class));
    }

    @Test
    void testCreateContactWithPrimary() {
        CustomerContactDTO dto = new CustomerContactDTO();
        dto.setCustomerId(100L);
        dto.setName("新联系人");
        dto.setPhone("13800138001");
        dto.setIsPrimary(1);

        CustomerContactServiceImpl spy = spy(contactService);
        doReturn((List) Collections.emptyList()).when(spy).<List>list(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(CustomerContact.class));

        // 由于 isPrimary=1 且列表为空，createContact 不会触发 updateBatchById
        assertDoesNotThrow(() -> spy.createContact(dto));
        verify(spy).save(any(CustomerContact.class));
    }

    // === updateContact 测试 ===

    @Test
    void testUpdateContact() {
        CustomerContactDTO dto = new CustomerContactDTO();
        dto.setId(1L);
        dto.setName("更新后的姓名");
        dto.setPhone("13800138999");

        CustomerContact existingContact = new CustomerContact();
        existingContact.setId(1L);
        existingContact.setCustomerId(100L);
        existingContact.setName("旧姓名");
        existingContact.setIsPrimary(0);

        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(existingContact).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(CustomerContact.class));

        assertDoesNotThrow(() -> spy.updateContact(dto));
        verify(spy).updateById(any(CustomerContact.class));
    }

    @Test
    void testUpdateContactNotFound() {
        CustomerContactDTO dto = new CustomerContactDTO();
        dto.setId(999L);
        dto.setName("更新后的姓名");

        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateContact(dto));
    }

    // === deleteContact 测试 ===

    @Test
    void testDeleteContact() {
        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(true).when(spy).removeById(1L);

        assertDoesNotThrow(() -> spy.deleteContact(1L));
        verify(spy).removeById(1L);
    }

    @Test
    void testDeleteContacts() {
        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteContacts(Arrays.asList(1L, 2L, 3L)));
        verify(spy).removeByIds(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testDeleteContactsEmpty() {
        CustomerContactServiceImpl spy = spy(contactService);
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteContacts(Collections.emptyList()));
        verify(spy).removeByIds(Collections.emptyList());
    }
}
