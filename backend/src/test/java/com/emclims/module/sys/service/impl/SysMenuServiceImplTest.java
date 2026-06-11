package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.MenuTreeNode;
import com.emclims.module.sys.entity.SysMenu;
import com.emclims.module.sys.entity.SysRoleMenu;
import com.emclims.module.sys.mapper.SysMenuMapper;
import com.emclims.module.sys.mapper.SysRoleMenuMapper;
import com.emclims.module.sys.vo.SysMenuVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysMenuServiceImpl 菜单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysMenuServiceImplTest {

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysMenuMapper menuMapper;

    private SysMenuServiceImpl menuService;

    @BeforeEach
    void setUp() throws Exception {
        menuService = new SysMenuServiceImpl(roleMenuMapper);
        // 通过反射设置 ServiceImpl 的 baseMapper 字段
        var baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(menuService, menuMapper);
    }

    private SysMenu createMenu(Long id, String name, Integer type, Long parentId, Integer sort, String permission) {
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setMenuName(name);
        menu.setMenuType(type);
        menu.setParentId(parentId);
        menu.setSort(sort);
        menu.setPermission(permission);
        return menu;
    }

    @Test
    void testGetMenuTree() {
        SysMenu menu1 = createMenu(1L, "系统管理", 1, 0L, 1, null);
        SysMenu menu2 = createMenu(2L, "用户管理", 2, 1L, 1, null);
        SysMenu menu3 = createMenu(3L, "新增用户", 3, 2L, 1, "sys:user:add");

        SysMenuServiceImpl spy = spy(menuService);
        List<SysMenu> allMenus = List.of(menu1, menu2, menu3);
        doReturn(allMenus).when(spy).list(any(LambdaQueryWrapper.class));

        List<SysMenuVO> tree = spy.getMenuTree();
        assertEquals(1, tree.size());
        assertEquals("系统管理", tree.get(0).getMenuName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("用户管理", tree.get(0).getChildren().get(0).getMenuName());
    }

    @Test
    void testCreateMenu() {
        SysMenu menu = createMenu(null, "新菜单", 2, 0L, 1, null);

        SysMenuServiceImpl spy = spy(menuService);
        doReturn(true).when(spy).save(any(SysMenu.class));

        spy.createMenu(menu);
        verify(spy).save(menu);
    }

    @Test
    void testCreateMenuWithNullParent() {
        SysMenu menu = new SysMenu();
        menu.setMenuName("顶级菜单");
        menu.setMenuType(1);

        SysMenuServiceImpl spy = spy(menuService);
        doReturn(true).when(spy).save(any(SysMenu.class));

        spy.createMenu(menu);
        assertEquals(0L, menu.getParentId());
    }

    @Test
    void testUpdateMenu() {
        SysMenu menu = createMenu(1L, "更新名称", 2, 0L, 1, null);

        SysMenuServiceImpl spy = spy(menuService);
        doReturn(true).when(spy).updateById(any(SysMenu.class));

        spy.updateMenu(menu);
        verify(spy).updateById(menu);
    }

    @Test
    void testDeleteMenuSuccess() {
        SysMenuServiceImpl spy = spy(menuService);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).removeById(1L);

        spy.deleteMenu(1L);
        verify(spy).removeById(1L);
    }

    @Test
    void testDeleteMenuHasChildren() {
        SysMenuServiceImpl spy = spy(menuService);
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.deleteMenu(1L), "该菜单下还有子菜单，不能删除");
        verify(spy, never()).removeById(any());
    }

    @Test
    void testGetMenuTreeByRoleId() {
        SysMenu menu1 = createMenu(1L, "用户管理", 2, 0L, 1, "sys:user:list");
        SysMenu menu2 = createMenu(2L, "角色管理", 2, 0L, 2, "sys:role:list");

        SysRoleMenu rm = new SysRoleMenu();
        rm.setRoleId(1L);
        rm.setMenuId(1L);

        SysMenuServiceImpl spy = spy(menuService);
        doReturn(List.of(menu1, menu2)).when(spy).list();
        when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(rm));

        List<MenuTreeNode> tree = spy.getMenuTreeByRoleId(1L);
        assertNotNull(tree);
        assertEquals(2, tree.size());
    }

    @Test
    void testGetMenuTreeByUserId() {
        SysMenu menu1 = createMenu(1L, "用户管理", 2, 0L, 1, "sys:user:list");
        SysMenu menu2 = createMenu(2L, "用户新增", 3, 1L, 1, "sys:user:add");

        SysMenuServiceImpl spy = spy(menuService);
        doReturn(List.of(menu1, menu2)).when(spy).list(any(LambdaQueryWrapper.class));

        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(List.of("sys:user:list"));

        List<MenuTreeNode> tree = spy.getMenuTreeByUserId(1L);
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals("用户管理", tree.get(0).getMenuName());
    }
}
