package com.emclims.module.standard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.standard.dto.StandardCategoryDTO;
import com.emclims.module.standard.dto.StandardCategoryQueryDTO;
import com.emclims.module.standard.entity.StandardCategory;
import com.emclims.module.standard.vo.StandardCategoryVO;

import java.util.List;

/**
 * 标准分类 Service 接口
 */
public interface StandardCategoryService extends IService<StandardCategory> {

    /**
     * 分页查询标准分类
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<StandardCategoryVO> pageCategories(StandardCategoryQueryDTO queryDTO);

    /**
     * 获取标准分类详情
     *
     * @param id 分类ID
     * @return 分类详情VO
     */
    StandardCategoryVO getCategoryDetail(Long id);

    /**
     * 新增标准分类
     *
     * @param dto 分类编辑DTO
     */
    void addCategory(StandardCategoryDTO dto);

    /**
     * 更新标准分类
     *
     * @param dto 分类编辑DTO
     */
    void updateCategory(StandardCategoryDTO dto);

    /**
     * 批量删除标准分类
     *
     * @param ids 分类ID列表
     */
    void deleteCategories(List<Long> ids);
}
