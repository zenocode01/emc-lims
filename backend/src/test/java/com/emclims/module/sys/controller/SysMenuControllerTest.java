package com.emclims.module.sys.controller;

import com.emclims.module.sys.dto.MenuTreeNode;
import com.emclims.module.sys.entity.SysMenu;
import com.emclims.module.sys.service.SysMenuService;
import com.emclims.module.sys.vo.SysMenuVO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysMenuController 菜单管理控制器单元测试
 */
class SysMenuControllerTest {

    private MockMvc mockMvc;
    private SysMenuService menuService;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        menuService = mock(SysMenuService.class);
        request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        request.setAttribute("permissions", List.of("sys:menu:list"));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        mockMvc = MockMvcBuilders.standaloneSetup(new SysMenuController(menuService)).build();
    }

    @Test
    void testTree() throws Exception {
        SysMenuVO vo = new SysMenuVO();
        vo.setMenuName("首页");

        when(menuService.getMenuTree()).thenReturn(List.of(vo));

        mockMvc.perform(get("/sys/menu/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].menuName").value("首页"));

        verify(menuService).getMenuTree();
    }

    @Test
    void testTreeByRole() throws Exception {
        MenuTreeNode node = new MenuTreeNode();
        node.setId(1L);
        node.setMenuName("首页");

        when(menuService.getMenuTreeByRoleId(1L)).thenReturn(List.of(node));

        mockMvc.perform(get("/sys/menu/tree-by-role")
                        .param("roleId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].menuName").value("首页"));

        verify(menuService).getMenuTreeByRoleId(1L);
    }

    @Test
    void testCurrentUserTree() throws Exception {
        MenuTreeNode node = new MenuTreeNode();
        node.setId(1L);
        node.setMenuName("首页");
        node.setMenuType(1);
        node.setParentId(0L);
        node.setSort(1);
        node.setStatus(1);
        node.setIsHidden(0);

        when(menuService.getMenuTreeByUserId(any())).thenReturn(List.of(node));

        mockMvc.perform(get("/sys/menu/current-user/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].menuName").value("首页"));

        verify(menuService).getMenuTreeByUserId(any());
    }

    @Test
    void testDetail() throws Exception {
        SysMenu menu = new SysMenu();
        menu.setId(1L);
        menu.setMenuName("首页");

        when(menuService.getById(1L)).thenReturn(menu);

        mockMvc.perform(get("/sys/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.menuName").value("首页"));

        verify(menuService).getById(1L);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(menuService).createMenu(any(SysMenu.class));

        mockMvc.perform(post("/sys/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuName\":\"新菜单\",\"path\":\"/new\",\"type\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(menuService).createMenu(any(SysMenu.class));
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(menuService).updateMenu(any(SysMenu.class));

        mockMvc.perform(put("/sys/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"menuName\":\"更新名称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(menuService).updateMenu(any(SysMenu.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(menuService).deleteMenu(1L);

        mockMvc.perform(delete("/sys/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(menuService).deleteMenu(1L);
    }
}
