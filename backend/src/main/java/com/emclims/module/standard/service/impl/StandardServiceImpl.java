package com.emclims.module.standard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.standard.dto.StandardDTO;
import com.emclims.module.standard.dto.StandardQueryDTO;
import com.emclims.module.standard.entity.Standard;
import com.emclims.module.standard.enums.StandardStatusEnum;
import com.emclims.module.standard.enums.StandardTypeEnum;
import com.emclims.module.standard.mapper.StandardMapper;
import com.emclims.module.standard.service.StandardService;
import com.emclims.module.standard.vo.StandardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 标准 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class StandardServiceImpl extends ServiceImpl<StandardMapper, Standard> implements StandardService {

    /** 状态名称映射 */
    private static final Map<String, String> STATUS_MAP = Map.of(
            "0", "禁用",
            "1", "启用"
    );

    /** 类型名称映射 */
    private static final Map<String, String> TYPE_MAP = Map.of(
            "emission", "发射",
            "immunity", "抗扰度"
    );

    /**
     * 分页查询标准
     */
    @Override
    public Page<StandardVO> pageStandards(StandardQueryDTO queryDTO) {
        LambdaQueryWrapper<Standard> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                Standard::getCode, queryDTO.getKeyword())
                .or()
                .like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        Standard::getName, queryDTO.getKeyword());

        // 类型筛选
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getType()),
                Standard::getType, queryDTO.getType());

        // 状态筛选
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getStatus()),
                Standard::getStatus, queryDTO.getStatus());

        // 生效日期范围
        wrapper.ge(queryDTO.getEffectiveDateStart() != null,
                Standard::getEffectiveDate, queryDTO.getEffectiveDateStart())
                .le(queryDTO.getEffectiveDateEnd() != null,
                        Standard::getEffectiveDate, queryDTO.getEffectiveDateEnd());

        // 按创建时间倒序
        wrapper.orderByDesc(Standard::getCreateTime);

        Page<Standard> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Standard> resultPage = this.page(page, wrapper);

        // 批量查询标准名称，用于填充分类中的标准名称
        List<StandardVO> voList = resultPage.getRecords().stream().map(standard -> {
            StandardVO vo = new StandardVO();
            vo.setId(standard.getId());
            vo.setCode(standard.getCode());
            vo.setName(standard.getName());
            vo.setVersion(standard.getVersion());
            vo.setIssuingOrg(standard.getIssuingOrg());
            vo.setEffectiveDate(standard.getEffectiveDate());
            vo.setExpiryDate(standard.getExpiryDate());
            vo.setStatus(standard.getStatus());
            vo.setStatusName(STATUS_MAP.getOrDefault(standard.getStatus(), standard.getStatus()));
            vo.setType(standard.getType());
            vo.setTypeName(TYPE_MAP.getOrDefault(standard.getType(), standard.getType()));
            vo.setRemark(standard.getRemark());
            vo.setCreateTime(standard.getCreateTime());
            vo.setUpdateTime(standard.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());

        Page<StandardVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setPages(resultPage.getPages());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取标准详情
     */
    @Override
    public StandardVO getStandardDetail(Long id) {
        Standard standard = this.getById(id);
        if (standard == null) {
            throw new BusinessException("标准不存在");
        }

        StandardVO vo = new StandardVO();
        vo.setId(standard.getId());
        vo.setCode(standard.getCode());
        vo.setName(standard.getName());
        vo.setVersion(standard.getVersion());
        vo.setIssuingOrg(standard.getIssuingOrg());
        vo.setEffectiveDate(standard.getEffectiveDate());
        vo.setExpiryDate(standard.getExpiryDate());
        vo.setStatus(standard.getStatus());
        vo.setStatusName(STATUS_MAP.getOrDefault(standard.getStatus(), standard.getStatus()));
        vo.setType(standard.getType());
        vo.setTypeName(TYPE_MAP.getOrDefault(standard.getType(), standard.getType()));
        vo.setRemark(standard.getRemark());
        vo.setCreateTime(standard.getCreateTime());
        vo.setUpdateTime(standard.getUpdateTime());
        return vo;
    }

    /**
     * 新增标准
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStandard(StandardDTO dto) {
        // 检查编号是否重复
        LambdaQueryWrapper<Standard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Standard::getCode, dto.getCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("标准编号已存在");
        }

        Standard standard = new Standard();
        standard.setCode(dto.getCode());
        standard.setName(dto.getName());
        standard.setVersion(dto.getVersion());
        standard.setIssuingOrg(dto.getIssuingOrg());
        standard.setEffectiveDate(dto.getEffectiveDate());
        standard.setExpiryDate(dto.getExpiryDate());
        standard.setStatus(dto.getStatus());
        standard.setType(dto.getType());
        standard.setRemark(dto.getRemark());
        this.save(standard);
    }

    /**
     * 更新标准
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStandard(StandardDTO dto) {
        Standard standard = this.getById(dto.getId());
        if (standard == null) {
            throw new BusinessException("标准不存在");
        }

        // 检查编号是否重复（排除自身）
        LambdaQueryWrapper<Standard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Standard::getCode, dto.getCode())
                .ne(Standard::getId, dto.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("标准编号已存在");
        }

        standard.setCode(dto.getCode());
        standard.setName(dto.getName());
        standard.setVersion(dto.getVersion());
        standard.setIssuingOrg(dto.getIssuingOrg());
        standard.setEffectiveDate(dto.getEffectiveDate());
        standard.setExpiryDate(dto.getExpiryDate());
        standard.setStatus(dto.getStatus());
        standard.setType(dto.getType());
        standard.setRemark(dto.getRemark());
        this.updateById(standard);
    }

    /**
     * 批量删除标准
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStandards(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的标准");
        }
        this.removeByIds(ids);
    }
}
