package com.emclims.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.entity.SysUserRole;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
import java.util.List;
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
    private final SysUserRoleMapper userRoleMapper;

    public DataPermissionLoader(SysUserMapper userMapper, SysRoleMapper roleMapper, 
                                SysDeptMapper deptMapper, SysUserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.deptMapper = deptMapper;
        this.userRoleMapper = userRoleMapper;
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

        // 查询用户所有角色，获取最大的 dataScope（1-全部 > 2-本部门 > 3-本部门及子部门 > 4-仅本人）
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            Integer maxDataScope = null;
            for (Long roleId : roleIds) {
                SysRole role = roleMapper.selectById(roleId);
                if (role != null && role.getDataScope() != null) {
                    // dataScope 越小权限越大，取最小值
                    if (maxDataScope == null || role.getDataScope() < maxDataScope) {
                        maxDataScope = role.getDataScope();
                    }
                }
            }
            if (maxDataScope != null) {
                DataPermissionContext.setDataScope(maxDataScope);
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
