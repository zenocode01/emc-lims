package com.emclims.module.customer.controller;

import com.emclims.module.customer.dto.CustomerContactDTO;
import com.emclims.module.customer.service.CustomerContactService;
import com.emclims.module.customer.vo.CustomerContactVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CustomerContactController 联系人控制器单元测试
 */
class CustomerContactControllerTest {

    private MockMvc mockMvc;
    private CustomerContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = mock(CustomerContactService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerContactController(contactService)).build();
    }

    @Test
    void testListByCustomerId() throws Exception {
        CustomerContactVO vo = new CustomerContactVO();
        vo.setName("张三");
        vo.setPhone("13800138000");

        when(contactService.listByCustomerId(100L)).thenReturn(List.of(vo));

        mockMvc.perform(get("/customer/contact/list/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("张三"));

        verify(contactService).listByCustomerId(100L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(contactService).createContact(any(CustomerContactDTO.class));

        mockMvc.perform(post("/customer/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":100,\"name\":\"张三\",\"phone\":\"13800138000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(contactService).createContact(any(CustomerContactDTO.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(contactService).updateContact(any(CustomerContactDTO.class));

        mockMvc.perform(put("/customer/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"customerId\":100,\"name\":\"更新姓名\",\"phone\":\"13900139000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(contactService).updateContact(any(CustomerContactDTO.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(contactService).deleteContact(1L);

        mockMvc.perform(delete("/customer/contact/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(contactService).deleteContact(1L);
    }

    @Test
    void testDeleteBatch() throws Exception {
        doNothing().when(contactService).deleteContacts(anyList());

        mockMvc.perform(delete("/customer/contact/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(contactService).deleteContacts(Arrays.asList(1L, 2L, 3L));
    }
}
