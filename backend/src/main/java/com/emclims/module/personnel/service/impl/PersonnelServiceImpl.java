package com.emclims.module.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.personnel.dto.PersonnelDTO;
import com.emclims.module.personnel.dto.PersonnelQueryDTO;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.mapper.PersonnelMapper;
import com.emclims.module.personnel.service.PersonnelService;
import com.emclims.module.personnel.vo.PersonnelExportVO;
import com.emclims.module.personnel.vo.PersonnelVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员档案 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelServiceImpl extends ServiceImpl<PersonnelMapper, Personnel> implements PersonnelService {

    private final SysUserMapper sysUserMapper;

    /**
     * 状态码映射
     */
    private static final java.util.Map<String, String> STATUS_MAP = new java.util.HashMap<String, String>() {{
        put("0", "停用");
        put("1", "启用");
    }};

    /**
     * 分页查询人员档案
     */
    @Override
    public Page<PersonnelVO> pagePersonnel(PersonnelQueryDTO queryDTO) {
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        Personnel::getName, queryDTO.getKeyword())
                .like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        Personnel::getIdCard, queryDTO.getKeyword())
                .eq(StrUtil.isNotBlank(queryDTO.getEducation()),
                        Personnel::getEducation, queryDTO.getEducation())
                .eq(StrUtil.isNotBlank(queryDTO.getTitle()),
                        Personnel::getTitle, queryDTO.getTitle())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()),
                        Personnel::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getHireDateStart() != null,
                        Personnel::getHireDate, queryDTO.getHireDateStart())
                .le(queryDTO.getHireDateEnd() != null,
                        Personnel::getHireDate, queryDTO.getHireDateEnd())
                .orderByDesc(Personnel::getCreateTime);

        Page<Personnel> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Personnel> resultPage = this.page(page, wrapper);

        List<PersonnelVO> voList = convertToVOList(resultPage.getRecords());
        Page<PersonnelVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取人员档案详情
     */
    @Override
    public PersonnelVO getPersonnelDetail(Long id) {
        Personnel personnel = this.getById(id);
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }
        List<Personnel> personnels = new ArrayList<>();
        personnels.add(personnel);
        List<PersonnelVO> vos = convertToVOList(personnels);
        return vos.isEmpty() ? null : vos.get(0);
    }

    /**
     * 新增人员档案
     */
    @Override
    public void addPersonnel(PersonnelDTO dto) {
        // 验证用户是否存在
        SysUser sysUser = sysUserMapper.selectById(dto.getUserId());
        if (sysUser == null) {
            throw new BusinessException("关联用户不存在");
        }

        Personnel personnel = new Personnel();
        BeanUtils.copyProperties(dto, personnel);
        this.save(personnel);
        log.info("新增人员档案，人员ID: {}, 姓名: {}", personnel.getId(), personnel.getName());
    }

    /**
     * 更新人员档案
     */
    @Override
    public void updatePersonnel(PersonnelDTO dto) {
        Personnel personnel = this.getById(dto.getId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        // 验证用户是否存在
        SysUser sysUser = sysUserMapper.selectById(dto.getUserId());
        if (sysUser == null) {
            throw new BusinessException("关联用户不存在");
        }

        BeanUtils.copyProperties(dto, personnel, "id", "createTime", "updateTime", "createBy", "updateBy", "deleted");
        this.updateById(personnel);
        log.info("更新人员档案，人员ID: {}, 姓名: {}", personnel.getId(), personnel.getName());
    }

    /**
     * 批量删除人员档案
     */
    @Override
    public void deletePersonnel(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除列表不能为空");
        }
        this.removeByIds(ids);
        log.info("批量删除人员档案，删除数量: {}", ids.size());
    }

    /**
     * 导出人员档案列表
     */
    @Override
    public List<PersonnelExportVO> exportPersonnel(PersonnelQueryDTO queryDTO) {
        log.debug("导出人员档案列表，关键字: {}", queryDTO.getKeyword());
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        Personnel::getName, queryDTO.getKeyword())
                .like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        Personnel::getIdCard, queryDTO.getKeyword())
                .eq(StrUtil.isNotBlank(queryDTO.getEducation()),
                        Personnel::getEducation, queryDTO.getEducation())
                .eq(StrUtil.isNotBlank(queryDTO.getTitle()),
                        Personnel::getTitle, queryDTO.getTitle())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()),
                        Personnel::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getHireDateStart() != null,
                        Personnel::getHireDate, queryDTO.getHireDateStart())
                .le(queryDTO.getHireDateEnd() != null,
                        Personnel::getHireDate, queryDTO.getHireDateEnd())
                .orderByDesc(Personnel::getCreateTime);

        List<Personnel> personnels = this.list(wrapper);
        return personnels.stream()
                .map(this::convertToExportVO)
                .collect(Collectors.toList());
    }

    /**
     * 将 Personnel 实体转换为导出 VO
     */
    private PersonnelExportVO convertToExportVO(Personnel personnel) {
        PersonnelExportVO vo = new PersonnelExportVO();
        BeanUtils.copyProperties(personnel, vo);
        vo.setStatusName(STATUS_MAP.getOrDefault(personnel.getStatus(), personnel.getStatus()));
        return vo;
    }

    /**
     * 将实体列表转换为 VO 列表，填充关联数据
     */
    private List<PersonnelVO> convertToVOList(List<Personnel> list) {
        return list.stream().map(personnel -> {
            PersonnelVO vo = new PersonnelVO();
            BeanUtils.copyProperties(personnel, vo);

            // 填充用户名称
            if (personnel.getUserId() != null) {
                SysUser sysUser = sysUserMapper.selectById(personnel.getUserId());
                if (sysUser != null) {
                    vo.setUserName(sysUser.getNickname());
                }
            }

            // 填充状态名称
            vo.setStatusName(STATUS_MAP.getOrDefault(personnel.getStatus(), personnel.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }
}
