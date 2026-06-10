package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.MenuTreeNode;
import com.emclims.module.sys.entity.SysMenu;
import com.emclims.module.sys.entity.SysRoleMenu;
import com.emclims.module.sys.mapper.SysMenuMapper;
import com.emclims.module.sys.mapper.SysRoleMenuMapper;
import com.emclims.module.sys.service.SysMenuService;
import com.emclims.module.sys.vo.SysMenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单 Service 实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper roleMenuMapper;

    public SysMenuServiceImpl(SysRoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<SysMenuVO> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = this.list(wrapper);

        List<SysMenuVO> voList = menus.stream().map(menu -> {
            SysMenuVO vo = new SysMenuVO();
            BeanUtils.copyProperties(menu, vo);
            return vo;
        }).collect(Collectors.toList());

        return buildTree(voList, 0L);
    }

    @Override
    public List<MenuTreeNode> getMenuTreeByRoleId(Long roleId) {
        // 查询所有菜单
        List<SysMenu> allMenus = this.list();

        // 查询该角色拥有的菜单ID
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> checkedMenuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        // 转换为树节点
        List<MenuTreeNode> nodes = allMenus.stream().map(menu -> {
            MenuTreeNode node = new MenuTreeNode();
            BeanUtils.copyProperties(menu, node);
            return node;
        }).collect(Collectors.toList());

        List<MenuTreeNode> tree = buildMenuTree(nodes, 0L);
        // 标记选中状态
        markChecked(tree, checkedMenuIds);
        return tree;
    }

    @Override
    public List<MenuTreeNode> getMenuTreeByUserId(Long userId) {
        // 查询用户拥有的权限标识
        List<String> permissions = this.baseMapper.selectPermissionsByUserId(userId);

        // 查询所有启用的菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> menus = this.list(wrapper);

        List<MenuTreeNode> nodes = menus.stream().map(menu -> {
            MenuTreeNode node = new MenuTreeNode();
            BeanUtils.copyProperties(menu, node);
            return node;
        }).collect(Collectors.toList());

        List<MenuTreeNode> tree = buildMenuTree(nodes, 0L);
        // 根据权限过滤（仅对按钮类型菜单做权限过滤）
        return tree.stream()
                .filter(menu -> isPermitted(menu, permissions))
                .collect(Collectors.toList());
    }

    @Override
    public void createMenu(SysMenu menu) {
        if (menu.getParentId() == null || menu.getParentId() == 0) {
            menu.setParentId(0L);
        }
        this.save(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        this.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该菜单下还有子菜单，不能删除");
        }
        this.removeById(id);
    }

    private List<SysMenuVO> buildTree(List<SysMenuVO> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysMenuVO::getSort))
                .map(menu -> {
                    menu.setChildren(buildTree(allMenus, menu.getId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }

    private List<MenuTreeNode> buildMenuTree(List<MenuTreeNode> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .sorted(Comparator.comparing(MenuTreeNode::getSort))
                .map(menu -> {
                    menu.setChildren(buildMenuTree(allMenus, menu.getId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }

    private void markChecked(List<MenuTreeNode> nodes, List<Long> checkedIds) {
        for (MenuTreeNode node : nodes) {
            if (checkedIds.contains(node.getId())) {
                // 标记为选中（可在后续扩展）
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                markChecked(node.getChildren(), checkedIds);
            }
        }
    }

    private boolean isPermitted(SysMenu menu, List<String> permissions) {
        // 目录和菜单类型：只要有子菜单可见即可
        if ("1".equals(menu.getMenuType()) || "2".equals(menu.getMenuType())) {
            return true;
        }
        // 按钮类型：需要有对应权限标识
        if ("3".equals(menu.getMenuType())) {
            if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
                return permissions.contains(menu.getPermission());
            }
            return true;
        }
        return true;
    }
}
