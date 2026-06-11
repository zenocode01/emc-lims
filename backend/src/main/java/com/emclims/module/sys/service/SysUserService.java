package com.emclims.module.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sys.dto.SysUserDTO;
import com.emclims.module.sys.dto.SysUserQueryDTO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.vo.SysUserVO;

import java.util.List;

/**
 * 用户 Service
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    Page<SysUserVO> pageUsers(SysUserQueryDTO queryDTO);

    /**
     * 根据ID获取用户详情
     */
    SysUserVO getUserDetail(Long id);

    /**
     * 新增用户
     */
    void createUser(SysUserDTO dto);

    /**
     * 更新用户
     */
    void updateUser(SysUserDTO dto);

    /**
     * 批量删除用户
     */
    void deleteUsers(List<Long> ids);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String oldPassword, String newPassword);

    /**
     * 修改状态
     */
    void updateStatus(Long id, Integer status);
}
