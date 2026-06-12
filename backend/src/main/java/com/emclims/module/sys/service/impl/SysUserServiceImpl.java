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
import com.emclims.module.sys.vo.SysUserExportVO;
import com.emclims.module.sys.vo.SysUserVO;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

        // 批量查询用户默认角色，避免 N+1
        java.util.Map<Long, SysRole> defaultRoleMap = buildDefaultRoleMapBatch(userPage.getRecords());

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
     * 批量构建用户默认角色映射，避免 N+1 查询
     */
    private java.util.Map<Long, SysRole> buildDefaultRoleMapBatch(List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        // 一次性查询所有用户的角色关系
        List<Long> userIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds)
        );
        
        // 收集所有角色 ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        
        // 一次性查询所有角色
        List<SysRole> roles = roleIds.isEmpty() ? List.of() : roleMapper.selectBatchIds(roleIds);
        java.util.Map<Long, SysRole> roleMap = roles.stream()
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));
        
        // 构建用户 ID -> 角色映射（取第一个角色作为默认角色）
        java.util.Map<Long, SysRole> defaultRoleMap = new java.util.HashMap<>();
        for (SysUserRole userRole : userRoles) {
            if (!defaultRoleMap.containsKey(userRole.getUserId()) && roleMap.containsKey(userRole.getRoleId())) {
                defaultRoleMap.put(userRole.getUserId(), roleMap.get(userRole.getRoleId()));
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

    @Override
    public java.util.List<SysUserExportVO> exportUsers(SysUserQueryDTO queryDTO) {
        log.debug("导出用户列表，关键字: {}, 部门ID: {}, 状态: {}", queryDTO.getKeyword(), queryDTO.getDeptId(), queryDTO.getStatus());

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

        List<SysUser> userList = this.list(wrapper);

        // 批量查询部门
        List<Long> deptIds = userList.stream()
                .map(SysUser::getDeptId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        java.util.Map<Long, SysDept> deptMap = deptIds.isEmpty() ? java.util.Collections.emptyMap() :
                deptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, d -> d));

        // 批量查询用户默认角色，避免 N+1
        java.util.Map<Long, SysRole> defaultRoleMap = buildDefaultRoleMapBatch(userList);

        // 转换为导出VO
        return userList.stream().map(user -> {
            SysUserExportVO vo = new SysUserExportVO();
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setSexName(convertSexName(user.getSex()));
            vo.setStatusName(convertStatusName(user.getStatus()));
            vo.setPost(user.getPost());
            vo.setEmployeeCode(user.getEmployeeCode());
            vo.setCreateTime(user.getCreateTime());
            populateDeptNameForExport(vo, user.getDeptId(), deptMap);
            populateDefaultRoleForExport(vo, user.getId(), defaultRoleMap);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 转换性别名称
     */
    private String convertSexName(Integer sex) {
        if (sex == null) {
            return "未知";
        }
        return switch (sex) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 转换状态名称
     */
    private String convertStatusName(Integer status) {
        if (status == null) {
            return "禁用";
        }
        return status == 1 ? "启用" : "禁用";
    }

    /**
     * 填充部门名称（导出用）
     */
    private void populateDeptNameForExport(SysUserExportVO vo, Long deptId, java.util.Map<Long, SysDept> deptMap) {
        if (deptId != null && deptMap.containsKey(deptId)) {
            vo.setDeptName(deptMap.get(deptId).getDeptName());
        }
    }

    /**
     * 填充默认角色信息（导出用）
     */
    private void populateDefaultRoleForExport(SysUserExportVO vo, Long userId, java.util.Map<Long, SysRole> defaultRoleMap) {
        SysRole role = defaultRoleMap.get(userId);
        if (role != null) {
            vo.setRoleName(role.getRoleName());
        }
    }

    @Override
    public java.util.List<SysUserVO> listUsers() {
        log.debug("获取所有用户列表");
        List<SysUser> userList = this.list();

        // 批量查询部门
        List<Long> deptIds = userList.stream()
                .map(SysUser::getDeptId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        java.util.Map<Long, SysDept> deptMap = deptIds.isEmpty() ? java.util.Collections.emptyMap() :
                deptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, d -> d));

        // 批量查询用户默认角色
        java.util.Map<Long, SysRole> defaultRoleMap = buildDefaultRoleMapBatch(userList);

        return userList.stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            populateDeptName(vo, user.getDeptId(), deptMap);
            populateDefaultRole(vo, user.getId(), defaultRoleMap);
            return vo;
        }).collect(Collectors.toList());
    }
}
