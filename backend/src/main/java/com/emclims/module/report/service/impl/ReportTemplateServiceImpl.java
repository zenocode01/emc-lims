package com.emclims.module.report.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.response.PageResult;
import com.emclims.module.report.dto.ReportTemplateDTO;
import com.emclims.module.report.dto.ReportTemplateQueryDTO;
import com.emclims.module.report.entity.ReportTemplate;
import com.emclims.module.report.mapper.ReportTemplateMapper;
import com.emclims.module.report.service.ReportTemplateService;
import com.emclims.module.report.vo.ReportTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 报告模板 Service 实现
 */
@Slf4j
@Service
public class ReportTemplateServiceImpl extends ServiceImpl<ReportTemplateMapper, ReportTemplate> implements ReportTemplateService {

    @Override
    public PageResult<ReportTemplateVO> pageTemplates(ReportTemplateQueryDTO queryDTO) {
        log.debug("分页查询报告模板");

        LambdaQueryWrapper<ReportTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), ReportTemplate::getTemplateName, queryDTO.getKeyword())
               .eq(StrUtil.isNotBlank(queryDTO.getTemplateType()), ReportTemplate::getTemplateType, queryDTO.getTemplateType())
               .eq(StrUtil.isNotBlank(queryDTO.getProductCategory()), ReportTemplate::getProductCategory, queryDTO.getProductCategory())
               .eq(queryDTO.getStatus() != null, ReportTemplate::getStatus, queryDTO.getStatus())
               .orderByDesc(ReportTemplate::getCreateTime);

        Page<ReportTemplate> page = this.page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        // 转换为 VO
        List<ReportTemplateVO> voList = page.getRecords().stream().map(template -> {
            ReportTemplateVO vo = new ReportTemplateVO();
            BeanUtils.copyProperties(template, vo);
            // 填充类型名称
            vo.setTemplateTypeName(getTemplateTypeName(template.getTemplateType()));
            vo.setStatusName(getStatusName(template.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), voList);
    }

    @Override
    public ReportTemplateVO getTemplateDetail(Long id) {
        log.debug("获取报告模板详情，模板 ID: {}", id);
        ReportTemplate template = this.getById(id);
        if (template == null) {
            throw new BusinessException("报告模板不存在");
        }

        ReportTemplateVO vo = new ReportTemplateVO();
        BeanUtils.copyProperties(template, vo);
        vo.setTemplateTypeName(getTemplateTypeName(template.getTemplateType()));
        vo.setStatusName(getStatusName(template.getStatus()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplate(ReportTemplateDTO dto) {
        log.info("创建报告模板，模板名称：{}", dto.getTemplateName());

        // 检查模板编码是否已存在
        long count = this.count(new LambdaQueryWrapper<ReportTemplate>()
                .eq(ReportTemplate::getTemplateCode, dto.getTemplateCode()));
        if (count > 0) {
            throw new BusinessException("模板编码已存在");
        }

        ReportTemplate template = new ReportTemplate();
        BeanUtils.copyProperties(dto, template);
        this.save(template);

        log.info("报告模板创建成功，模板 ID: {}", template.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(ReportTemplateDTO dto) {
        log.info("更新报告模板，模板 ID: {}", dto.getId());

        ReportTemplate template = this.getById(dto.getId());
        if (template == null) {
            throw new BusinessException("报告模板不存在");
        }

        BeanUtils.copyProperties(dto, template, "id", "createTime");
        this.updateById(template);

        log.info("报告模板更新成功，模板 ID: {}", template.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        log.info("删除报告模板，模板 ID: {}", id);
        ReportTemplate template = this.getById(id);
        if (template == null) {
            throw new BusinessException("报告模板不存在");
        }
        this.removeById(id);
        log.info("报告模板删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplates(Long[] ids) {
        log.info("批量删除报告模板，模板 ID 列表: {}", ids);
        this.removeByIds(List.of(ids));
        log.info("批量删除报告模板成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateStatus(Long id, Integer status) {
        log.info("更新报告模板状态，模板 ID: {}, 状态: {}", id, status);
        ReportTemplate template = this.getById(id);
        if (template == null) {
            throw new BusinessException("报告模板不存在");
        }
        template.setStatus(status);
        this.updateById(template);
        log.info("报告模板状态更新成功");
    }

    /**
     * 获取模板类型名称
     */
    private String getTemplateTypeName(String templateType) {
        if (templateType == null) return "-";
        switch (templateType) {
            case "emission": return "发射测试";
            case "immunity": return "抗扰度测试";
            case "general": return "通用测试";
            default: return templateType;
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "-";
        return status == 1 ? "启用" : "停用";
    }
}
