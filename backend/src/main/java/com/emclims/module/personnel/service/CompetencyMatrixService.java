package com.emclims.module.personnel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.personnel.dto.CompetencyMatrixDTO;
import com.emclims.module.personnel.dto.CompetencyMatrixQueryDTO;
import com.emclims.module.personnel.entity.CompetencyMatrix;
import com.emclims.module.personnel.vo.CompetencyMatrixVO;

/**
 * 能力矩阵 Service
 */
public interface CompetencyMatrixService extends IService<CompetencyMatrix> {

    /**
     * 分页查询能力矩阵
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<CompetencyMatrixVO> pageCompetencyMatrix(CompetencyMatrixQueryDTO queryDTO);

    /**
     * 获取能力矩阵详情
     *
     * @param id 能力矩阵ID
     * @return 能力矩阵详情
     */
    CompetencyMatrixVO getCompetencyDetail(Long id);

    /**
     * 新增能力矩阵记录
     *
     * @param dto 能力矩阵信息
     */
    void addCompetencyMatrix(CompetencyMatrixDTO dto);

    /**
     * 更新能力矩阵记录
     *
     * @param dto 能力矩阵信息
     */
    void updateCompetencyMatrix(CompetencyMatrixDTO dto);

    /**
     * 批量删除能力矩阵记录
     *
     * @param ids 能力矩阵ID列表
     */
    void deleteCompetencyMatrices(java.util.List<Long> ids);

    /**
     * 根据人员ID查询能力矩阵列表
     *
     * @param personnelId 人员ID
     * @return 能力矩阵列表
     */
    java.util.List<CompetencyMatrixVO> listByPersonnelId(Long personnelId);

    /**
     * 根据人员和测试项目查询能力矩阵
     *
     * @param personnelId 人员ID
     * @param testItemType 测试项目类型
     * @return 能力矩阵记录
     */
    CompetencyMatrixVO getByPersonnelAndItemType(Long personnelId, String testItemType);
}
