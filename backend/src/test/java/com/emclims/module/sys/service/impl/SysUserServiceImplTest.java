package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.SysUserDTO;
import com.emclims.module.sys.dto.SysUserQueryDTO;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
import com.emclims.module.sys.vo.SysUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysUserServiceImpl 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @Mock
    private SysDeptMapper deptMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SysUserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new SysUserServiceImpl(deptMapper, roleMapper, userRoleMapper, passwordEncoder);
    }

    @Test
    void testCreateUserSuccess() {
        SysUserDTO dto = new SysUserDTO();
        dto.setPhone("13800138000");
        dto.setPassword("password123");
        dto.setNickname("新用户");

        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");

        // 使用 spy 来模拟 count 和 save 行为
        SysUserServiceImpl spy = spy(userService);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).save(any(SysUser.class));

        assertDoesNotThrow(() -> spy.createUser(dto));
        verify(spy).save(any(SysUser.class));
    }

    @Test
    void testCreateUserDuplicatePhone() {
        SysUserDTO dto = new SysUserDTO();
        dto.setPhone("13800138000");
        dto.setPassword("password123");

        SysUserServiceImpl spy = spy(userService);
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.createUser(dto), "手机号已存在");
        verify(spy, never()).save(any());
    }

    @Test
    void testUpdateUserSuccess() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setPhone("13800138000");
        dto.setNickname("更新名称");

        SysUser existingUser = new SysUser();
        existingUser.setId(1L);
        existingUser.setPhone("13900139000");
        existingUser.setNickname("旧名称");

        SysUserServiceImpl spy = spy(userService);
        doReturn(existingUser).when(spy).getById(1L);
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).updateById(any(SysUser.class));

        assertDoesNotThrow(() -> spy.updateUser(dto));
        verify(spy).updateById(any(SysUser.class));
    }

    @Test
    void testUpdateUserNotFound() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(999L);

        SysUserServiceImpl spy = spy(userService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.updateUser(dto), "用户不存在");
    }

    @Test
    void testUpdateUserDuplicatePhone() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setPhone("13800138000");

        SysUser existingUser = new SysUser();
        existingUser.setId(1L);
        existingUser.setPhone("13900139000");

        SysUserServiceImpl spy = spy(userService);
        doReturn(existingUser).when(spy).getById(1L);
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.updateUser(dto), "手机号已被其他用户使用");
    }

    @Test
    void testDeleteUsers() {
        SysUserServiceImpl spy = spy(userService);
        doReturn(true).when(spy).removeByIds(anyList());

        spy.deleteUsers(List.of(1L, 2L, 3L));
        verify(spy).removeByIds(List.of(1L, 2L, 3L));
    }

    @Test
    void testResetPassword() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setPassword("oldPassword");

        SysUserServiceImpl spy = spy(userService);
        doReturn(user).when(spy).getById(1L);
        when(passwordEncoder.encode("newPass123")).thenReturn("$2a$10$newEncoded");
        doReturn(true).when(spy).updateById(any(SysUser.class));

        spy.resetPassword(1L, "newPass123");
        assertEquals("$2a$10$newEncoded", user.getPassword());
        verify(spy).updateById(user);
    }

    @Test
    void testResetPasswordUserNotFound() {
        SysUserServiceImpl spy = spy(userService);
        doReturn(null).when(spy).getById(1L);

        assertThrows(BusinessException.class, () -> spy.resetPassword(1L, "newPass"));
    }

    @Test
    void testUpdateStatus() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setStatus(1);

        SysUserServiceImpl spy = spy(userService);
        doReturn(user).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(SysUser.class));

        spy.updateStatus(1L, 0);
        assertEquals(0, user.getStatus());
        verify(spy).updateById(user);
    }

    @Test
    void testUpdateStatusUserNotFound() {
        SysUserServiceImpl spy = spy(userService);
        doReturn(null).when(spy).getById(1L);

        assertThrows(BusinessException.class, () -> spy.updateStatus(1L, 0));
    }

    @Test
    void testGetUserDetail() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setNickname("测试用户");
        user.setDeptId(10L);
        user.setRoleId(20L);

        SysDept dept = new SysDept();
        dept.setId(10L);
        dept.setDeptName("测试部");

        SysRole role = new SysRole();
        role.setId(20L);
        role.setRoleName("测试员");
        role.setRoleCode("tester");

        SysUserServiceImpl spy = spy(userService);
        doReturn(user).when(spy).getById(1L);
        when(deptMapper.selectById(10L)).thenReturn(dept);
        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectRoleIdsByUserId(1L)).thenReturn(List.of(20L));

        SysUserVO vo = spy.getUserDetail(1L);
        assertNotNull(vo);
        assertEquals("测试用户", vo.getNickname());
        assertEquals("测试部", vo.getDeptName());
        assertEquals("测试员", vo.getRoleName());
        assertEquals("tester", vo.getRoleCode());
    }

    @Test
    void testGetUserDetailNotFound() {
        SysUserServiceImpl spy = spy(userService);
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.getUserDetail(999L));
    }

    @Test
    void testPageUsers() {
        SysUserQueryDTO queryDTO = new SysUserQueryDTO();
        queryDTO.setKeyword("138");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        SysUser user = new SysUser();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setNickname("测试用户");
        user.setDeptId(10L);

        Page<SysUser> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(user));

        SysUserServiceImpl spy = spy(userService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        SysDept dept = new SysDept();
        dept.setId(10L);
        dept.setDeptName("测试部");
        when(deptMapper.selectById(10L)).thenReturn(dept);

        Page<SysUserVO> result = spy.pageUsers(queryDTO);
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("测试部", result.getRecords().get(0).getDeptName());
    }
}
