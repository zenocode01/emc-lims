package com.emclims.common.security;

import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.entity.SysUserRole;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据权限加载器测试
 */
@SpringBootTest
class DataPermissionLoaderTest {

    @Autowired
    private DataPermissionLoader dataPermissionLoader;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @BeforeEach
    void setUp() {
        DataPermissionContext.clear();
    }

    @AfterEach
    void tearDown() {
        DataPermissionContext.clear();
    }

    @Test
    void testLoadNullUserId() {
        dataPermissionLoader.load(null);
        assertNull(DataPermissionContext.getUserId());
        assertNull(DataPermissionContext.getDeptId());
    }

    @Test
    void testLoadNonExistentUser() {
        dataPermissionLoader.load(999999L);
        assertNull(DataPermissionContext.getUserId());
        assertNull(DataPermissionContext.getDeptId());
    }

    @Test
    void testLoadUserWithRole() {
        // 创建测试部门
        SysDept dept = new SysDept();
        dept.setDeptName("测试部门");
        dept.setDeptCode("TEST_DEPT");
        dept.setSort(1);
        dept.setStatus(1);
        deptMapper.insert(dept);

        // 创建测试角色（dataScope=2: 本部门）
        SysRole role = new SysRole();
        role.setRoleName("测试角色");
        role.setRoleCode("TEST_ROLE");
        role.setDataScope(2);
        role.setSort(1);
        role.setStatus(1);
        roleMapper.insert(role);

        // 创建测试用户
        SysUser user = new SysUser();
        user.setUsername("testuser1");
        user.setPhone("13800000001");
        user.setPassword("$2a$10$testpasswordhash"); // BCrypt hash
        user.setNickname("测试用户");
        user.setDeptId(dept.getId());
        user.setStatus(1);
        userMapper.insert(user);

        // 关联用户角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);

        try {
            // 加载数据权限
            dataPermissionLoader.load(user.getId());

            // 验证
            assertEquals(user.getId(), DataPermissionContext.getUserId());
            assertEquals(dept.getId(), DataPermissionContext.getDeptId());
            assertEquals(2, DataPermissionContext.getDataScope());
        } finally {
            userRoleMapper.deleteById(userRole.getId());
            userMapper.deleteById(user.getId());
            roleMapper.deleteById(role.getId());
            deptMapper.deleteById(dept.getId());
        }
    }

    @Test
    void testLoadUserWithAllDataScope() {
        // 创建测试部门
        SysDept dept = new SysDept();
        dept.setDeptName("测试部门2");
        dept.setDeptCode("TEST_DEPT2");
        dept.setSort(1);
        dept.setStatus(1);
        deptMapper.insert(dept);

        // 创建测试角色（dataScope=1: 全部数据）
        SysRole role = new SysRole();
        role.setRoleName("全部数据角色");
        role.setRoleCode("ALL_DATA_ROLE");
        role.setDataScope(1);
        role.setSort(1);
        role.setStatus(1);
        roleMapper.insert(role);

        // 创建测试用户
        SysUser user = new SysUser();
        user.setUsername("testuser2");
        user.setPhone("13800000002");
        user.setPassword("$2a$10$testpasswordhash");
        user.setNickname("全部数据用户");
        user.setDeptId(dept.getId());
        user.setStatus(1);
        userMapper.insert(user);

        // 关联用户角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);

        try {
            dataPermissionLoader.load(user.getId());

            assertEquals(user.getId(), DataPermissionContext.getUserId());
            assertEquals(1, DataPermissionContext.getDataScope()); // 全部数据
        } finally {
            userRoleMapper.deleteById(userRole.getId());
            userMapper.deleteById(user.getId());
            roleMapper.deleteById(role.getId());
            deptMapper.deleteById(dept.getId());
        }
    }
}
