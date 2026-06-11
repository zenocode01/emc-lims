package com.emclims.module.sample.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.module.sample.dto.SampleDTO;
import com.emclims.module.sample.dto.SampleQueryDTO;
import com.emclims.module.sample.dto.SampleStatusDTO;
import com.emclims.module.sample.service.SampleService;
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;
import com.emclims.common.response.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SampleController 样品控制器单元测试
 */
class SampleControllerTest {

    private MockMvc mockMvc;
    private SampleService sampleService;

    @BeforeEach
    void setUp() {
        sampleService = mock(SampleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SampleController(sampleService))
                .setValidator(new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean())
                .build();
    }

    @Test
    void testPage() throws Exception {
        Page<SampleVO> page = new Page<>(1, 10, 1);
        SampleVO vo = new SampleVO();
        vo.setProductName("样品1");
        page.setRecords(List.of(vo));

        when(sampleService.pageSamples(any(SampleQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/sample/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].productName").value("样品1"));

        verify(sampleService).pageSamples(any(SampleQueryDTO.class));
    }

    @Test
    void testDetail() throws Exception {
        SampleVO vo = new SampleVO();
        vo.setId(1L);
        vo.setProductName("样品1");

        when(sampleService.getSampleDetail(1L)).thenReturn(vo);

        mockMvc.perform(get("/sample/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productName").value("样品1"));

        verify(sampleService).getSampleDetail(1L);
    }

    @Test
    void testReceive() throws Exception {
        doNothing().when(sampleService).receiveSample(any(SampleDTO.class));

        mockMvc.perform(post("/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":100,\"productName\":\"新样品\",\"receiveDate\":\"2025-06-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sampleService).receiveSample(any(SampleDTO.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(sampleService).updateSample(any(SampleDTO.class));

        // Use @PutMapping which maps to /sample (class-level @RequestMapping)
        mockMvc.perform(put("/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"customerId\":100,\"productName\":\"更新名称\",\"receiveDate\":\"2025-06-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sampleService).updateSample(any(SampleDTO.class));
    }

    @Test
    void testDeleteBatch() throws Exception {
        doNothing().when(sampleService).deleteSamples(anyList());

        mockMvc.perform(delete("/sample/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sampleService).deleteSamples(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testChangeStatus() throws Exception {
        doNothing().when(sampleService).changeStatus(any(SampleStatusDTO.class));

        mockMvc.perform(put("/sample/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sampleId\":1,\"toStatus\":\"testing\",\"remark\":\"开始测试\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(sampleService).changeStatus(any(SampleStatusDTO.class));
    }

    @Test
    void testLogs() throws Exception {
        SampleLogVO log = new SampleLogVO();
        log.setFromStatus("pending");
        log.setToStatus("received");

        when(sampleService.getSampleLogs(1L)).thenReturn(List.of(log));

        mockMvc.perform(get("/sample/1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].fromStatus").value("pending"));

        verify(sampleService).getSampleLogs(1L);
    }
}
