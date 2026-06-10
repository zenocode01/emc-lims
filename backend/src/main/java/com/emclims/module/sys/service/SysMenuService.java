package com.emclims.module.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sys.dto.MenuTreeNode;
import com.emclims.module.sys.entity.SysMenu;
import com.emclims.module.sys.vo.SysMenuVO;

import java.util.List;

/**
 * 菜单 Service
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树
     */
    List<SysMenuVO> getMenuTree();

    /**
     * 根据角色获取菜单树
     */
    List<MenuTreeNode> getMenuTreeByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限菜单树
     */
    List<MenuTreeNode> getMenuTreeByUserId(Long userId);

    /**
     * 新增菜单
     */
    void createMenu(SysMenu menu);

    /**
     * 更新菜单
     */
    void updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);
}
