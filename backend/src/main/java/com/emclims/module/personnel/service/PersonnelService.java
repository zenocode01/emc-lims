package com.emclims.module.personnel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.personnel.dto.PersonnelDTO;
import com.emclims.module.personnel.dto.PersonnelQueryDTO;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.vo.PersonnelExportVO;
import com.emclims.module.personnel.vo.PersonnelVO;

/**
 * 人员档案 Service
 */
public interface PersonnelService extends IService<Personnel> {

    /**
     * 分页查询人员档案
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PersonnelVO> pagePersonnel(PersonnelQueryDTO queryDTO);

    /**
     * 获取人员档案详情
     *
     * @param id 人员ID
     * @return 人员详情
     */
    PersonnelVO getPersonnelDetail(Long id);

    /**
     * 新增人员档案
     *
     * @param dto 人员信息
     */
    void addPersonnel(PersonnelDTO dto);

    /**
     * 更新人员档案
     *
     * @param dto 人员信息
     */
    void updatePersonnel(PersonnelDTO dto);

    /**
     * 批量删除人员档案
     *
     * @param ids 人员ID列表
     */
    void deletePersonnel(java.util.List<Long> ids);

    /**
     * 导出人员档案列表
     *
     * @param queryDTO 查询条件
     * @return 导出列表
     */
    java.util.List<PersonnelExportVO> exportPersonnel(PersonnelQueryDTO queryDTO);
}
