package com.emclims.module.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.service.SysRoleService;
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
 * SysRoleController 角色管理控制器单元测试
 */
class SysRoleControllerTest {

    private MockMvc mockMvc;
    private SysRoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = mock(SysRoleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SysRoleController(roleService)).build();
    }

    @Test
    void testPage() throws Exception {
        Page<SysRole> page = new Page<>(1, 10, 1);
        SysRole role = new SysRole();
        role.setRoleName("管理员");
        page.setRecords(List.of(role));

        when(roleService.page(any(Page.class))).thenReturn(page);

        mockMvc.perform(get("/sys/role/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].roleName").value("管理员"));

        verify(roleService).page(any(PageDTO.class));
    }

    @Test
    void testDetail() throws Exception {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setRoleName("管理员");

        when(roleService.getRoleDetail(1L)).thenReturn(role);

        mockMvc.perform(get("/sys/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.roleName").value("管理员"));

        verify(roleService).getRoleDetail(1L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(roleService).createRole(any(SysRole.class));

        mockMvc.perform(post("/sys/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\":\"新角色\",\"roleCode\":\"new_role\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).createRole(any(SysRole.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(roleService).updateRole(any(SysRole.class));

        mockMvc.perform(put("/sys/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"roleName\":\"更新名称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).updateRole(any(SysRole.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/sys/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).deleteRole(1L);
    }

    @Test
    void testDeleteBatch() throws Exception {
        doNothing().when(roleService).deleteRoles(anyList());

        mockMvc.perform(delete("/sys/role/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).deleteRoles(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testUpdateStatus() throws Exception {
        doNothing().when(roleService).updateRoleStatus(eq(1L), eq(0));

        mockMvc.perform(put("/sys/role/1/status")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).updateRoleStatus(1L, 0);
    }

    @Test
    void testGrantMenus() throws Exception {
        doNothing().when(roleService).grantMenus(any(RoleMenuDTO.class));

        mockMvc.perform(post("/sys/role/grant-menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":1,\"menuIds\":[1,2,3]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).grantMenus(any(RoleMenuDTO.class));
    }

    @Test
    void testGetMenuIds() throws Exception {
        when(roleService.getMenuIdsByRoleId(1L)).thenReturn(List.of(1L, 2L, 3L));

        mockMvc.perform(get("/sys/role/1/menu-ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(roleService).getMenuIdsByRoleId(1L);
    }
}
