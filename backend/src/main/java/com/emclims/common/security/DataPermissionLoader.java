package com.emclims.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

/**
 * 数据权限加载器
 * 根据用户ID加载数据权限信息到 DataPermissionContext
 */
@Component
public class DataPermissionLoader {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;

    public DataPermissionLoader(SysUserMapper userMapper, SysRoleMapper roleMapper, SysDeptMapper deptMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.deptMapper = deptMapper;
    }

    /**
     * 加载用户数据权限
     */
    public void load(Long userId) {
        if (userId == null) {
            return;
        }

        // 查询用户信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        // 设置用户ID和部门ID
        DataPermissionContext.setUserId(userId);
        DataPermissionContext.setDeptId(user.getDeptId());

        // 查询用户角色，获取最大的 dataScope
        if (user.getRoleId() != null) {
            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null && role.getDataScope() != null) {
                DataPermissionContext.setDataScope(role.getDataScope());
            }
        }
    }

    /**
     * 清除数据权限上下文
     */
    public void clear() {
        DataPermissionContext.clear();
    }
}
