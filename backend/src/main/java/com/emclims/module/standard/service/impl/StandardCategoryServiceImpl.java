package com.emclims.module.standard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.standard.dto.StandardCategoryDTO;
import com.emclims.module.standard.dto.StandardCategoryQueryDTO;
import com.emclims.module.standard.entity.Standard;
import com.emclims.module.standard.entity.StandardCategory;
import com.emclims.module.standard.mapper.StandardCategoryMapper;
import com.emclims.module.standard.mapper.StandardMapper;
import com.emclims.module.standard.service.StandardCategoryService;
import com.emclims.module.standard.vo.StandardCategoryVO;
import com.emclims.module.standard.vo.StandardCategoryVO.StandardInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标准分类 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class StandardCategoryServiceImpl extends ServiceImpl<StandardCategoryMapper, StandardCategory>
        implements StandardCategoryService {

    private final StandardMapper standardMapper;

    /**
     * 分页查询标准分类
     */
    @Override
    public Page<StandardCategoryVO> pageCategories(StandardCategoryQueryDTO queryDTO) {
        LambdaQueryWrapper<StandardCategory> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                StandardCategory::getName, queryDTO.getKeyword())
                .or()
                .like(StrUtil.isNotBlank(queryDTO.getKeyword()),
                        StandardCategory::getProductType, queryDTO.getKeyword());

        // 产品类型筛选
        wrapper.eq(StrUtil.isNotBlank(queryDTO.getProductType()),
                StandardCategory::getProductType, queryDTO.getProductType());

        // 按创建时间倒序
        wrapper.orderByDesc(StandardCategory::getCreateTime);

        Page<StandardCategory> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<StandardCategory> resultPage = this.page(page, wrapper);

        // 提取所有分类中的标准ID
        List<Long> allStandardIds = resultPage.getRecords().stream()
                .flatMap(category -> (category.getApplicableStandards() != null
                        ? category.getApplicableStandards().stream()
                        : java.util.stream.Stream.empty())
                )
                .distinct()
                .collect(Collectors.toList());

        // 批量查询标准详情（用于前端展示）
        final Map<Long, StandardInfo> standardInfoMap;
        if (!allStandardIds.isEmpty()) {
            List<Standard> standards = standardMapper.selectBatchIds(allStandardIds);
            standardInfoMap = standards.stream()
                    .collect(Collectors.toMap(Standard::getId,
                            s -> {
                                StandardInfo info = new StandardInfo();
                                info.setId(s.getId());
                                info.setCode(s.getCode());
                                info.setName(s.getName());
                                info.setVersion(s.getVersion());
                                return info;
                            }
                    ));
        } else {
            standardInfoMap = Collections.emptyMap();
        }

        // 转换为VO
        List<StandardCategoryVO> voList = resultPage.getRecords().stream().map(category -> {
            StandardCategoryVO vo = new StandardCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setApplicableStandards(category.getApplicableStandards());
            vo.setProductType(category.getProductType());
            vo.setRemark(category.getRemark());
            vo.setCreateTime(category.getCreateTime());
            vo.setUpdateTime(category.getUpdateTime());

            // 填充标准详细信息
            List<Long> standardIds = category.getApplicableStandards();
            if (standardIds != null && !standardInfoMap.isEmpty()) {
                List<StandardInfo> infoList = standardIds.stream()
                        .map(id -> standardInfoMap.getOrDefault(id, createEmptyStandardInfo(id)))
                        .collect(Collectors.toList());
                vo.setApplicableStandardDetails(infoList);
            } else {
                vo.setApplicableStandardDetails(Collections.emptyList());
            }
            return vo;
        }).collect(Collectors.toList());

        Page<StandardCategoryVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setPages(resultPage.getPages());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 创建空的标准信息
     */
    private StandardInfo createEmptyStandardInfo(Long id) {
        StandardInfo info = new StandardInfo();
        info.setId(id);
        info.setCode("未知标准");
        info.setName("未知标准");
        info.setVersion("");
        return info;
    }

    /**
     * 获取标准分类详情
     */
    @Override
    public StandardCategoryVO getCategoryDetail(Long id) {
        StandardCategory category = this.getById(id);
        if (category == null) {
            throw new BusinessException("标准分类不存在");
        }

        StandardCategoryVO vo = new StandardCategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setApplicableStandards(category.getApplicableStandards());
        vo.setProductType(category.getProductType());
        vo.setRemark(category.getRemark());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());

        // 填充标准详细信息
        if (category.getApplicableStandards() != null && !category.getApplicableStandards().isEmpty()) {
            List<Standard> standards = standardMapper.selectBatchIds(category.getApplicableStandards());
            List<StandardInfo> infoList = standards.stream().map(s -> {
                StandardInfo info = new StandardInfo();
                info.setId(s.getId());
                info.setCode(s.getCode());
                info.setName(s.getName());
                info.setVersion(s.getVersion());
                return info;
            }).collect(Collectors.toList());
            vo.setApplicableStandardDetails(infoList);
        } else {
            vo.setApplicableStandardDetails(Collections.emptyList());
        }
        return vo;
    }

    /**
     * 新增标准分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(StandardCategoryDTO dto) {
        StandardCategory category = new StandardCategory();
        category.setName(dto.getName());
        category.setApplicableStandards(dto.getApplicableStandards());
        category.setProductType(dto.getProductType());
        category.setRemark(dto.getRemark());
        this.save(category);
    }

    /**
     * 更新标准分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(StandardCategoryDTO dto) {
        StandardCategory category = this.getById(dto.getId());
        if (category == null) {
            throw new BusinessException("标准分类不存在");
        }

        category.setName(dto.getName());
        category.setApplicableStandards(dto.getApplicableStandards());
        category.setProductType(dto.getProductType());
        category.setRemark(dto.getRemark());
        this.updateById(category);
    }

    /**
     * 批量删除标准分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategories(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的分类");
        }
        this.removeByIds(ids);
    }
}
