package com.emclims.module.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.sys.dto.SysUserDTO;
import com.emclims.module.sys.dto.SysUserQueryDTO;
import com.emclims.module.sys.service.SysUserService;
import com.emclims.module.sys.vo.SysUserVO;
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
 * SysUserController 用户管理控制器单元测试
 */
class SysUserControllerTest {

    private MockMvc mockMvc;
    private SysUserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(SysUserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SysUserController(userService)).build();
    }

    @Test
    void testPage() throws Exception {
        Page<SysUserVO> page = new Page<>(1, 10, 1);
        SysUserVO vo = new SysUserVO();
        vo.setPhone("13800138000");
        vo.setNickname("管理员");
        page.setRecords(List.of(vo));

        when(userService.pageUsers(any(SysUserQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/sys/user/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].nickname").value("管理员"));

        verify(userService).pageUsers(any(SysUserQueryDTO.class));
    }

    @Test
    void testDetail() throws Exception {
        SysUserVO vo = new SysUserVO();
        vo.setId(1L);
        vo.setPhone("13800138000");
        vo.setNickname("管理员");

        when(userService.getUserDetail(1L)).thenReturn(vo);

        mockMvc.perform(get("/sys/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("管理员"));

        verify(userService).getUserDetail(1L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(userService).createUser(any(SysUserDTO.class));

        mockMvc.perform(post("/sys/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"13800138001\",\"password\":\"password123\",\"nickname\":\"新用户\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).createUser(any(SysUserDTO.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(userService).updateUser(any(SysUserDTO.class));

        mockMvc.perform(put("/sys/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"phone\":\"13800138002\",\"nickname\":\"新昵称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).updateUser(any(SysUserDTO.class));
    }

    @Test
    void testDeleteBatch() throws Exception {
        doNothing().when(userService).deleteUsers(anyList());

        mockMvc.perform(delete("/sys/user/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).deleteUsers(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testResetPassword() throws Exception {
        doNothing().when(userService).resetPassword(eq(1L), anyString(), anyString());

        mockMvc.perform(put("/sys/user/1/password")
                        .param("oldPassword", "oldpass")
                        .param("newPassword", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).resetPassword(eq(1L), eq("oldpass"), eq("newpass123"));
    }

    @Test
    void testUpdateStatus() throws Exception {
        doNothing().when(userService).updateStatus(eq(1L), eq(0));

        mockMvc.perform(put("/sys/user/1/status")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).updateStatus(1L, 0);
    }
}
