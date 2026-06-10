package com.emclims.module.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;

import java.util.List;

/**
 * 角色 Service
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据ID获取角色详情
     */
    SysRole getRoleDetail(Long id);

    /**
     * 新增角色
     */
    void createRole(SysRole role);

    /**
     * 更新角色
     */
    void updateRole(SysRole role);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 批量删除角色
     */
    void deleteRoles(List<Long> ids);

    /**
     * 更新角色状态
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 角色授权菜单
     */
    void grantMenus(RoleMenuDTO dto);

    /**
     * 根据角色ID获取菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
