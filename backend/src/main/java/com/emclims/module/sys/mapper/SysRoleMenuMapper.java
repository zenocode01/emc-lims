package com.emclims.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sys.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID删除所有菜单关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     */
    int batchInsert(@Param("list") List<SysRoleMenu> roleMenus);
}
