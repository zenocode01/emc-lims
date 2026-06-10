package com.emclims.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sys.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);
}
