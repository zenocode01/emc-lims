package com.emclims.common.security;

import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
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
    private final SysUserRoleMapper userRoleMapper;

    public DataPermissionLoader(SysUserMapper userMapper, SysRoleMapper roleMapper,
                                SysUserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 加载用户数据权限
     */
    public void load(Long userId) {
        if (userId == null) {
            return;
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        DataPermissionContext.setUserId(userId);
        DataPermissionContext.setDeptId(user.getDeptId());

        loadUserRolePermission(userId);
    }

    /**
     * 加载用户角色权限，取最小 dataScope（权限最大）
     */
    private void loadUserRolePermission(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        Integer minDataScope = findMinDataScope(roleIds);
        if (minDataScope != null) {
            DataPermissionContext.setDataScope(minDataScope);
        }
    }

    /**
     * 查找最小 dataScope（1-全部 2-本部门 3-本部门及子部门 4-仅本人）
     */
    private Integer findMinDataScope(List<Long> roleIds) {
        return roleMapper.selectBatchIds(roleIds).stream()
                .filter(r -> r != null && r.getDataScope() != null)
                .mapToInt(SysRole::getDataScope)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /**
     * 清除数据权限上下文
     */
    public void clear() {
        DataPermissionContext.clear();
    }
}
