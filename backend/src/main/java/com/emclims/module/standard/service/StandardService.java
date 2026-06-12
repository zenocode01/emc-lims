package com.emclims.module.standard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.standard.dto.StandardDTO;
import com.emclims.module.standard.dto.StandardQueryDTO;
import com.emclims.module.standard.entity.Standard;
import com.emclims.module.standard.vo.StandardExportVO;
import com.emclims.module.standard.vo.StandardVO;

import java.util.List;

/**
 * 标准 Service 接口
 */
public interface StandardService extends IService<Standard> {

    /**
     * 分页查询标准
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<StandardVO> pageStandards(StandardQueryDTO queryDTO);

    /**
     * 获取标准详情
     *
     * @param id 标准ID
     * @return 标准详情VO
     */
    StandardVO getStandardDetail(Long id);

    /**
     * 新增标准
     *
     * @param dto 标准编辑DTO
     */
    void addStandard(StandardDTO dto);

    /**
     * 更新标准
     *
     * @param dto 标准编辑DTO
     */
    void updateStandard(StandardDTO dto);

    /**
     * 批量删除标准
     *
     * @param ids 标准ID列表
     */
    void deleteStandards(List<Long> ids);

    /**
     * 导出标准列表
     *
     * @param queryDTO 查询条件
     * @return 标准导出列表
     */
    List<StandardExportVO> exportStandards(StandardQueryDTO queryDTO);
}
