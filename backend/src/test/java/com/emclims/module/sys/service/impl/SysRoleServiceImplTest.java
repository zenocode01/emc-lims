package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysRoleMenu;
import com.emclims.module.sys.entity.SysUserRole;
import com.emclims.module.sys.mapper.SysRoleMenuMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
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
 * SysRoleServiceImpl 角色服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysRoleServiceImplTest {

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    private SysRoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new SysRoleServiceImpl(roleMenuMapper, sysUserRoleMapper);
    }

    @Test
    void testCreateRoleSuccess() {
        SysRole role = new SysRole();
        role.setRoleName("测试员");
        role.setRoleCode("tester");

        SysRoleServiceImpl spy = spy(roleService);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(SysRole.class));

        assertDoesNotThrow(() -> spy.createRole(role));
        verify(spy).save(role);
    }

    @Test
    void testCreateRoleDuplicateCode() {
        SysRole role = new SysRole();
        role.setRoleName("测试员");
        role.setRoleCode("tester");

        SysRoleServiceImpl spy = spy(roleService);
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.createRole(role), "角色编码已存在");
        verify(spy, never()).save(any());
    }

    @Test
    void testUpdateRole() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setRoleName("更新角色名");

        SysRoleServiceImpl spy = spy(roleService);
        doReturn(true).when(spy).updateById(any(SysRole.class));

        spy.updateRole(role);
        verify(spy).updateById(role);
    }

    @Test
    void testDeleteRole() {
        SysRoleServiceImpl spy = spy(roleService);
        doReturn(0L).when(sysUserRoleMapper).selectCount(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).removeById(1L);

        spy.deleteRole(1L);
        verify(spy).removeById(1L);
    }

    @Test
    void testDeleteRoleWithUserRoleRef() {
        SysRoleServiceImpl spy = spy(roleService);
        doReturn(1L).when(sysUserRoleMapper).selectCount(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.deleteRole(1L), "该角色已被用户关联，无法删除");
        verify(spy, never()).removeById(any());
    }

    @Test
    void testDeleteRoles() {
        SysRoleServiceImpl spy = spy(roleService);
        doReturn(0L).when(sysUserRoleMapper).selectCount(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).removeByIds(anyList());

        spy.deleteRoles(List.of(1L, 2L));
        verify(spy).removeByIds(List.of(1L, 2L));
    }

    @Test
    @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
    void testDeleteRolesWithUserRoleRef() {
        SysRoleServiceImpl spy = spy(roleService);
        doReturn(0L).when(sysUserRoleMapper).selectCount(any(LambdaQueryWrapper.class));
        doReturn(1L).when(sysUserRoleMapper).selectCount(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).removeByIds(anyList());

        assertThrows(BusinessException.class, () -> spy.deleteRoles(List.of(1L, 2L)), "角色ID 2 已被用户关联，无法删除");
        verify(spy, never()).removeByIds(anyList());
    }

    @Test
    void testUpdateRoleStatus() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setStatus(1);

        SysRoleServiceImpl spy = spy(roleService);
        doReturn(role).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(SysRole.class));

        spy.updateRoleStatus(1L, 0);
        assertEquals(0, role.getStatus());
        verify(spy).updateById(role);
    }

    @Test
    void testUpdateRoleStatusNotFound() {
        SysRoleServiceImpl spy = spy(roleService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateRoleStatus(999L, 0));
    }

    @Test
    void testGrantMenus() {
        RoleMenuDTO dto = new RoleMenuDTO();
        dto.setRoleId(1L);
        dto.setMenuIds(List.of(10L, 20L, 30L));

        roleService.grantMenus(dto);
        verify(roleMenuMapper).deleteByRoleId(1L);
        verify(roleMenuMapper).batchInsert(argThat(list -> list.size() == 3));
    }

    @Test
    void testGrantMenusEmptyList() {
        RoleMenuDTO dto = new RoleMenuDTO();
        dto.setRoleId(1L);
        dto.setMenuIds(List.of());

        roleService.grantMenus(dto);
        verify(roleMenuMapper).deleteByRoleId(1L);
        verify(roleMenuMapper, never()).batchInsert(anyList());
    }

    @Test
    void testGetMenuIdsByRoleId() {
        SysRoleMenu rm1 = new SysRoleMenu();
        rm1.setId(1L);
        rm1.setRoleId(1L);
        rm1.setMenuId(10L);

        SysRoleMenu rm2 = new SysRoleMenu();
        rm2.setId(2L);
        rm2.setRoleId(1L);
        rm2.setMenuId(20L);

        when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(rm1, rm2));

        List<Long> menuIds = roleService.getMenuIdsByRoleId(1L);
        assertEquals(2, menuIds.size());
        assertTrue(menuIds.contains(10L));
        assertTrue(menuIds.contains(20L));
    }

    @Test
    void testGetRoleDetail() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setRoleName("管理员");

        SysRoleServiceImpl spy = spy(roleService);
        doReturn(role).when(spy).getById(1L);

        SysRole result = spy.getRoleDetail(1L);
        assertNotNull(result);
        assertEquals("管理员", result.getRoleName());
    }
}
