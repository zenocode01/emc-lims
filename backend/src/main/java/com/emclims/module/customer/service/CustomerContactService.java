package com.emclims.module.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.customer.dto.CustomerContactDTO;
import com.emclims.module.customer.entity.CustomerContact;
import com.emclims.module.customer.vo.CustomerContactVO;

import java.util.List;

/**
 * 联系人 Service
 */
public interface CustomerContactService extends IService<CustomerContact> {

    /**
     * 根据客户ID获取联系人列表
     */
    List<CustomerContactVO> listByCustomerId(Long customerId);

    /**
     * 新增联系人
     */
    void createContact(CustomerContactDTO dto);

    /**
     * 更新联系人
     */
    void updateContact(CustomerContactDTO dto);

    /**
     * 删除联系人
     */
    void deleteContact(Long id);

    /**
     * 批量删除联系人
     */
    void deleteContacts(List<Long> ids);
}
