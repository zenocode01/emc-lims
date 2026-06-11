package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysRoleMenu;
import com.emclims.module.sys.entity.SysUserRole;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysRoleMenuMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
import com.emclims.module.sys.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public SysRoleServiceImpl(SysRoleMenuMapper roleMenuMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.roleMenuMapper = roleMenuMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public SysRole getRoleDetail(Long id) {
        log.debug("获取角色详情，角色ID: {}", id);
        return this.getById(id);
    }

    @Override
    public void createRole(SysRole role) {
        log.info("创建角色，角色编码: {}, 角色名称: {}", role.getRoleCode(), role.getRoleName());
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
        log.info("更新角色信息，角色ID: {}, 角色编码: {}", role.getId(), role.getRoleCode());
        this.updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        log.info("删除角色，角色ID: {}", id);

        // 检查是否有用户关联该角色
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getRoleId, id);
        Long userRoleCount = sysUserRoleMapper.selectCount(userRoleWrapper);
        if (userRoleCount > 0) {
            throw new BusinessException("该角色已被用户关联，无法删除");
        }

        this.removeById(id);
    }

    @Override
    @Transactional
    public void deleteRoles(List<Long> ids) {
        log.info("批量删除角色，角色ID列表: {}", ids);

        // 检查是否有任何角色被用户关联
        for (Long roleId : ids) {
            LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
            userRoleWrapper.eq(SysUserRole::getRoleId, roleId);
            Long userRoleCount = sysUserRoleMapper.selectCount(userRoleWrapper);
            if (userRoleCount > 0) {
                throw new BusinessException("角色ID " + roleId + " 已被用户关联，无法删除");
            }
        }

        this.removeByIds(ids);
    }

    @Override
    public void updateRoleStatus(Long id, Integer status) {
        log.info("更新角色状态，角色ID: {}, 状态: {}", id, status);
        SysRole role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setStatus(status);
        this.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantMenus(RoleMenuDTO dto) {
        log.info("授权角色菜单，角色ID: {}, 菜单数量: {}", dto.getRoleId(), dto.getMenuIds() != null ? dto.getMenuIds().size() : 0);
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
