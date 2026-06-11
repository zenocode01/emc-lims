package com.emclims.module.sys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.dto.SysUserDTO;
import com.emclims.module.sys.dto.SysUserQueryDTO;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.entity.SysUserRole;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.sys.mapper.SysUserRoleMapper;
import com.emclims.module.sys.service.SysUserService;
import com.emclims.module.sys.vo.SysUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysDeptMapper deptMapper, SysRoleMapper roleMapper,
                              SysUserRoleMapper userRoleMapper,
                              org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.deptMapper = deptMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<SysUserVO> pageUsers(SysUserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getPhone, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getNickname, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getEmployeeCode, queryDTO.getKeyword())
               .eq(queryDTO.getDeptId() != null, SysUser::getDeptId, queryDTO.getDeptId())
               .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
               .between(queryDTO.getCreateTimeStart() != null && queryDTO.getCreateTimeEnd() != null,
                       SysUser::getCreateTime, queryDTO.getCreateTimeStart(), queryDTO.getCreateTimeEnd())
               .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = this.page(page, wrapper);

        // 按角色筛选
        if (queryDTO.getRoleId() != null) {
            List<Long> userIdsWithRole = userRoleMapper.selectUserIdsByRoleId(queryDTO.getRoleId());
            if (!userPage.getRecords().isEmpty()) {
                List<SysUser> filtered = userPage.getRecords().stream()
                        .filter(u -> userIdsWithRole.contains(u.getId()))
                        .collect(Collectors.toList());
                userPage.getRecords().clear();
                userPage.getRecords().addAll(filtered);
                userPage.setTotal(userPage.getRecords().size());
            }
        }

        // 填充部门名和角色列表
        List<SysUserVO> voList = userPage.getRecords().stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            }
            // 从关联表查询角色列表
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
            if (roleIds != null && !roleIds.isEmpty()) {
                // 设置第一个角色为默认角色（兼容旧版）
                SysRole defaultRole = roleMapper.selectById(roleIds.get(0));
                if (defaultRole != null) {
                    vo.setRoleName(defaultRole.getRoleName());
                    vo.setRoleCode(defaultRole.getRoleCode());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        Page<SysUserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SysUserVO getUserDetail(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);

        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }
        // 从关联表查询角色列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        if (roleIds != null && !roleIds.isEmpty()) {
            // 设置第一个角色为默认角色（兼容旧版）
            SysRole defaultRole = roleMapper.selectById(roleIds.get(0));
            if (defaultRole != null) {
                vo.setRoleName(defaultRole.getRoleName());
                vo.setRoleCode(defaultRole.getRoleCode());
                vo.setRoleId(defaultRole.getId());
            }
        }
        return vo;
    }

    @Override
    public void createUser(SysUserDTO dto) {
        // 检查手机号是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, dto.getPhone());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        this.save(user);

        // 保存角色关联
        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    public void updateUser(SysUserDTO dto) {
        SysUser user = this.getById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否被其他用户使用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, dto.getPhone())
               .ne(SysUser::getId, dto.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已被其他用户使用");
        }

        BeanUtils.copyProperties(dto, user, "password", "roleIds");
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        this.updateById(user);

        // 更新角色关联：先删除旧的，再插入新的
        userRoleMapper.deleteByUserId(user.getId());
        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    public void deleteUsers(List<Long> ids) {
        // 先删除用户角色关联
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }
        this.removeByIds(ids);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<SysUserRole> userRoles = roleIds.stream()
                .map(roleId -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());
        userRoleMapper.batchInsert(userRoles);
    }
}
