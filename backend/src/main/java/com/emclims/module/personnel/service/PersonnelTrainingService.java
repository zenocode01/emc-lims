package com.emclims.module.personnel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.personnel.dto.PersonnelTrainingDTO;
import com.emclims.module.personnel.dto.PersonnelTrainingQueryDTO;
import com.emclims.module.personnel.entity.PersonnelTraining;
import com.emclims.module.personnel.vo.PersonnelTrainingVO;

/**
 * 培训记录 Service
 */
public interface PersonnelTrainingService extends IService<PersonnelTraining> {

    /**
     * 分页查询培训记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PersonnelTrainingVO> pageTrainings(PersonnelTrainingQueryDTO queryDTO);

    /**
     * 获取培训记录详情
     *
     * @param id 培训记录ID
     * @return 培训详情
     */
    PersonnelTrainingVO getTrainingDetail(Long id);

    /**
     * 新增培训记录
     *
     * @param dto 培训信息
     */
    void addTraining(PersonnelTrainingDTO dto);

    /**
     * 更新培训记录
     *
     * @param dto 培训信息
     */
    void updateTraining(PersonnelTrainingDTO dto);

    /**
     * 批量删除培训记录
     *
     * @param ids 培训记录ID列表
     */
    void deleteTrainings(java.util.List<Long> ids);

    /**
     * 根据人员ID查询培训记录列表
     *
     * @param personnelId 人员ID
     * @return 培训记录列表
     */
    java.util.List<PersonnelTrainingVO> listByPersonnelId(Long personnelId);
}
