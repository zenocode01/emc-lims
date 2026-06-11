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
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SampleMapper sampleMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysDeptMapper deptMapper, SysRoleMapper roleMapper,
                              SysUserRoleMapper userRoleMapper,
                              SampleMapper sampleMapper,
                              org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.deptMapper = deptMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.sampleMapper = sampleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<SysUserVO> pageUsers(SysUserQueryDTO queryDTO) {
        log.debug("查询用户列表，关键字: {}, 部门ID: {}, 状态: {}", queryDTO.getKeyword(), queryDTO.getDeptId(), queryDTO.getStatus());
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getPhone, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getNickname, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getEmployeeCode, queryDTO.getKeyword())
               .eq(queryDTO.getDeptId() != null, SysUser::getDeptId, queryDTO.getDeptId())
               .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
               .between(queryDTO.getCreateTimeStart() != null && queryDTO.getCreateTimeEnd() != null,
                       SysUser::getCreateTime, queryDTO.getCreateTimeStart(), queryDTO.getCreateTimeEnd())
               .exists(queryDTO.getRoleId() != null,
                       "SELECT 1 FROM sys_user_role WHERE user_id = sys_user.id AND role_id = {0}", queryDTO.getRoleId())
               .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = this.page(page, wrapper);

        // 批量查询部门
        List<Long> deptIds = userPage.getRecords().stream()
                .map(SysUser::getDeptId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        java.util.Map<Long, SysDept> deptMap = deptIds.isEmpty() ? java.util.Collections.emptyMap() :
                deptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, d -> d));

        // 批量查询用户默认角色
        java.util.Map<Long, SysRole> defaultRoleMap = buildDefaultRoleMap(userPage.getRecords());

        // 填充部门名和角色列表
        List<SysUserVO> voList = userPage.getRecords().stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            populateDeptName(vo, user.getDeptId(), deptMap);
            populateDefaultRole(vo, user.getId(), defaultRoleMap);
            return vo;
        }).collect(Collectors.toList());

        Page<SysUserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SysUserVO getUserDetail(Long id) {
        log.debug("获取用户详情，用户ID: {}", id);
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
        log.info("创建用户，手机号: {}, 工号: {}", dto.getPhone(), dto.getEmployeeCode());
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
        log.info("更新用户信息，用户ID: {}, 手机号: {}", dto.getId(), dto.getPhone());
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
    @Transactional
    public void deleteUsers(List<Long> ids) {
        log.info("删除用户，用户ID列表: {}", ids);

        // 检查是否有业务引用（样品关联）
        for (Long userId : ids) {
            LambdaQueryWrapper<Sample> sampleWrapper = new LambdaQueryWrapper<>();
            sampleWrapper.eq(Sample::getTesterId, userId)
                         .or()
                         .eq(Sample::getReceiveBy, userId);
            Long sampleCount = sampleMapper.selectCount(sampleWrapper);
            if (sampleCount > 0) {
                throw new BusinessException("用户ID " + userId + " 关联了 " + sampleCount + " 个样品，无法删除");
            }
        }

        // 先删除用户角色关联
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }
        this.removeByIds(ids);
    }

    @Override
    public void resetPassword(Long id, String oldPassword, String newPassword) {
        log.info("重置用户密码，用户ID: {}", id);
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("更新用户状态，用户ID: {}, 状态: {}", id, status);
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

    /**
     * 构建用户默认角色映射
     */
    private java.util.Map<Long, SysRole> buildDefaultRoleMap(List<SysUser> users) {
        java.util.Map<Long, SysRole> defaultRoleMap = new java.util.HashMap<>();
        for (SysUser user : users) {
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
            if (roleIds != null && !roleIds.isEmpty()) {
                SysRole role = roleMapper.selectById(roleIds.get(0));
                if (role != null) {
                    defaultRoleMap.put(user.getId(), role);
                }
            }
        }
        return defaultRoleMap;
    }

    /**
     * 填充部门名称
     */
    private void populateDeptName(SysUserVO vo, Long deptId, java.util.Map<Long, SysDept> deptMap) {
        if (deptId != null && deptMap.containsKey(deptId)) {
            vo.setDeptName(deptMap.get(deptId).getDeptName());
        }
    }

    /**
     * 填充默认角色信息
     */
    private void populateDefaultRole(SysUserVO vo, Long userId, java.util.Map<Long, SysRole> defaultRoleMap) {
        SysRole role = defaultRoleMap.get(userId);
        if (role != null) {
            vo.setRoleName(role.getRoleName());
            vo.setRoleCode(role.getRoleCode());
        }
    }
}
