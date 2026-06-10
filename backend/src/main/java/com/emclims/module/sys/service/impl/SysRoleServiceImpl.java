package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysRoleMenu;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysRoleMenuMapper;
import com.emclims.module.sys.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;

    public SysRoleServiceImpl(SysRoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public SysRole getRoleDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public void createRole(SysRole role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        this.save(role);
    }

    @Override
    public void updateRole(SysRole role) {
        this.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        this.removeById(id);
    }

    @Override
    public void deleteRoles(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void updateRoleStatus(Long id, Integer status) {
        SysRole role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setStatus(status);
        this.updateById(role);
    }

    @Override
    public void grantMenus(RoleMenuDTO dto) {
        // 先删除该角色的所有菜单关联
        roleMenuMapper.deleteByRoleId(dto.getRoleId());
        // 再插入新的菜单关联
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            List<SysRoleMenu> roleMenus = dto.getMenuIds().stream().map(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(dto.getRoleId());
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());
            roleMenuMapper.batchInsert(roleMenus);
        }
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
