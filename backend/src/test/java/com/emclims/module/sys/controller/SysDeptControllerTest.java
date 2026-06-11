package com.emclims.module.sys.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.service.SysDeptService;
import com.emclims.module.sys.vo.SysDeptVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysDeptController 部门管理控制器单元测试
 */
class SysDeptControllerTest {

    private MockMvc mockMvc;
    private SysDeptService deptService;

    @BeforeEach
    void setUp() {
        deptService = mock(SysDeptService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SysDeptController(deptService)).build();
    }

    @Test
    void testTree() throws Exception {
        SysDeptVO vo = new SysDeptVO();
        vo.setDeptName("总公司");

        when(deptService.getDeptTree()).thenReturn(List.of(vo));

        mockMvc.perform(get("/sys/dept/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].deptName").value("总公司"));

        verify(deptService).getDeptTree();
    }

    @Test
    void testDetail() throws Exception {
        SysDeptVO vo = new SysDeptVO();
        vo.setId(1L);
        vo.setDeptName("技术部");

        when(deptService.getDeptDetail(1L)).thenReturn(vo);

        mockMvc.perform(get("/sys/dept/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deptName").value("技术部"));

        verify(deptService).getDeptDetail(1L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(deptService).createDept(any(SysDept.class));

        mockMvc.perform(post("/sys/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deptName\":\"新部门\",\"parentId\":0,\"sort\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).createDept(any(SysDept.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(deptService).updateDept(any(SysDept.class));

        mockMvc.perform(put("/sys/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"deptName\":\"更新名称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).updateDept(any(SysDept.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(deptService).deleteDept(1L);

        mockMvc.perform(delete("/sys/dept/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(deptService).deleteDept(1L);
    }
}
