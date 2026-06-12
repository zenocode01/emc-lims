package com.emclims.module.personnel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.personnel.dto.PersonnelAuthorizationDTO;
import com.emclims.module.personnel.dto.PersonnelAuthorizationQueryDTO;
import com.emclims.module.personnel.entity.PersonnelAuthorization;
import com.emclims.module.personnel.vo.PersonnelAuthorizationVO;

/**
 * 授权上岗记录 Service
 */
public interface PersonnelAuthorizationService extends IService<PersonnelAuthorization> {

    /**
     * 分页查询授权上岗记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PersonnelAuthorizationVO> pageAuthorizations(PersonnelAuthorizationQueryDTO queryDTO);

    /**
     * 获取授权上岗记录详情
     *
     * @param id 授权记录ID
     * @return 授权详情
     */
    PersonnelAuthorizationVO getAuthorizationDetail(Long id);

    /**
     * 新增授权上岗记录
     *
     * @param dto 授权信息
     */
    void addAuthorization(PersonnelAuthorizationDTO dto);

    /**
     * 更新授权上岗记录
     *
     * @param dto 授权信息
     */
    void updateAuthorization(PersonnelAuthorizationDTO dto);

    /**
     * 批量删除授权上岗记录
     *
     * @param ids 授权记录ID列表
     */
    void deleteAuthorizations(java.util.List<Long> ids);

    /**
     * 根据人员ID查询授权记录列表
     *
     * @param personnelId 人员ID
     * @return 授权记录列表
     */
    java.util.List<PersonnelAuthorizationVO> listByPersonnelId(Long personnelId);
}
