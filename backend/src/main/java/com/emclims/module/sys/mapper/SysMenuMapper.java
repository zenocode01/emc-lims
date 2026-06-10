package com.emclims.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sys.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单 Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色ID查询菜单列表
     */
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限标识列表
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
