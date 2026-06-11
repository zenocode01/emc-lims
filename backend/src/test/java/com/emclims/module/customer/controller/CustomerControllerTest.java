package com.emclims.module.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.customer.dto.CustomerDTO;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.emclims.module.customer.service.CustomerService;
import com.emclims.module.customer.vo.CustomerExportVO;
import com.emclims.module.customer.vo.CustomerVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CustomerController 客户控制器单元测试
 */
class CustomerControllerTest {

    private MockMvc mockMvc;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(customerService)).build();
    }

    @Test
    void testPage() throws Exception {
        Page<CustomerVO> page = new Page<>(1, 10, 1);
        CustomerVO vo = new CustomerVO();
        vo.setName("测试客户");
        page.setRecords(List.of(vo));

        when(customerService.pageCustomers(any(CustomerQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/customer/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("测试客户"));

        verify(customerService).pageCustomers(any(CustomerQueryDTO.class));
    }

    @Test
    void testDetail() throws Exception {
        CustomerVO vo = new CustomerVO();
        vo.setId(1L);
        vo.setName("测试客户");

        when(customerService.getCustomerDetail(1L)).thenReturn(vo);

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试客户"));

        verify(customerService).getCustomerDetail(1L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(customerService).createCustomer(any(CustomerDTO.class));

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"新客户\",\"contact\":\"张三\",\"phone\":\"13800138000\",\"type\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(customerService).updateCustomer(any(CustomerDTO.class));

        mockMvc.perform(put("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"更新名称\",\"contact\":\"李四\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).updateCustomer(any(CustomerDTO.class));
    }

    @Test
    void testDeleteBatch() throws Exception {
        doNothing().when(customerService).deleteCustomers(anyList());

        mockMvc.perform(delete("/customer/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).deleteCustomers(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testUpdateStatus() throws Exception {
        doNothing().when(customerService).updateStatus(eq(1L), eq(0));

        mockMvc.perform(put("/customer/1/status")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(customerService).updateStatus(1L, 0);
    }

    // 导出功能测试（需要 EasyExcel 依赖支持）
    // @Test
    // void testExport() throws Exception {
    //     CustomerExportVO vo = new CustomerExportVO();
    //     vo.setName("测试客户");
    //     when(customerService.exportCustomers(any(CustomerQueryDTO.class))).thenReturn(List.of(vo));

    //     mockMvc.perform(get("/customer/export")
    //                     .accept(MediaType.parseMediaType(
    //                             "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
    //             .andExpect(status().isOk());

    //     verify(customerService).exportCustomers(any(CustomerQueryDTO.class));
    // }
}
