package com.emclims.module.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.common.response.PageResult;
import com.emclims.module.report.dto.ReportTemplateDTO;
import com.emclims.module.report.dto.ReportTemplateQueryDTO;
import com.emclims.module.report.entity.ReportTemplate;
import com.emclims.module.report.vo.ReportTemplateVO;

/**
 * 报告模板 Service
 */
public interface ReportTemplateService extends IService<ReportTemplate> {

    /**
     * 分页查询报告模板
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ReportTemplateVO> pageTemplates(ReportTemplateQueryDTO queryDTO);

    /**
     * 获取模板详情
     * @param id 模板 ID
     * @return 模板详情
     */
    ReportTemplateVO getTemplateDetail(Long id);

    /**
     * 创建报告模板
     * @param dto 模板 DTO
     */
    void createTemplate(ReportTemplateDTO dto);

    /**
     * 更新报告模板
     * @param dto 模板 DTO
     */
    void updateTemplate(ReportTemplateDTO dto);

    /**
     * 删除报告模板
     * @param id 模板 ID
     */
    void deleteTemplate(Long id);

    /**
     * 批量删除报告模板
     * @param ids 模板 ID 列表
     */
    void deleteTemplates(Long[] ids);

    /**
     * 更新模板状态
     * @param id 模板 ID
     * @param status 状态
     */
    void updateTemplateStatus(Long id, Integer status);
}
