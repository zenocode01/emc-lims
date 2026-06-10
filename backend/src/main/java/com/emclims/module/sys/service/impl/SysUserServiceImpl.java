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
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.mapper.SysRoleMapper;
import com.emclims.module.sys.mapper.SysUserMapper;
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
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysDeptMapper deptMapper, SysRoleMapper roleMapper,
                              org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.deptMapper = deptMapper;
        this.roleMapper = roleMapper;
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
               .eq(queryDTO.getRoleId() != null, SysUser::getRoleId, queryDTO.getRoleId())
               .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
               .between(queryDTO.getCreateTimeStart() != null && queryDTO.getCreateTimeEnd() != null,
                       SysUser::getCreateTime, queryDTO.getCreateTimeStart(), queryDTO.getCreateTimeEnd())
               .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = this.page(page, wrapper);

        // 填充部门名和角色名
        List<SysUserVO> voList = userPage.getRecords().stream().map(user -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(user, vo);
            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            }
            if (user.getRoleId() != null) {
                SysRole role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    vo.setRoleName(role.getRoleName());
                    vo.setRoleCode(role.getRoleCode());
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
        if (user.getRoleId() != null) {
            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                vo.setRoleName(role.getRoleName());
                vo.setRoleCode(role.getRoleCode());
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

        BeanUtils.copyProperties(dto, user, "password");
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        this.updateById(user);
    }

    @Override
    public void deleteUsers(List<Long> ids) {
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
}
