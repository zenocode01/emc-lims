package com.emclims.module.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.dto.CustomerContactDTO;
import com.emclims.module.customer.entity.CustomerContact;
import com.emclims.module.customer.mapper.CustomerContactMapper;
import com.emclims.module.customer.service.CustomerContactService;
import com.emclims.module.customer.vo.CustomerContactVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人 Service 实现
 */
@Slf4j
@Service
public class CustomerContactServiceImpl extends ServiceImpl<CustomerContactMapper, CustomerContact> implements CustomerContactService {

    @Override
    public List<CustomerContactVO> listByCustomerId(Long customerId) {
        log.debug("查询客户联系人，客户ID: {}", customerId);
        LambdaQueryWrapper<CustomerContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerContact::getCustomerId, customerId)
               .orderByDesc(CustomerContact::getIsPrimary)
               .orderByDesc(CustomerContact::getCreateTime);

        return this.list(wrapper).stream().map(contact -> {
            CustomerContactVO vo = new CustomerContactVO();
            BeanUtils.copyProperties(contact, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void createContact(CustomerContactDTO dto) {
        log.info("新增联系人，客户ID: {}, 姓名: {}", dto.getCustomerId(), dto.getName());
        CustomerContact contact = new CustomerContact();
        BeanUtils.copyProperties(dto, contact);

        // 如果是首次添加或设为主要联系人，更新其他联系人状态
        if (contact.getIsPrimary() != null && contact.getIsPrimary() == 1) {
            clearPrimaryContact(dto.getCustomerId());
        }

        this.save(contact);
    }

    @Override
    public void updateContact(CustomerContactDTO dto) {
        log.info("更新联系人信息，联系人ID: {}", dto.getId());
        CustomerContact contact = this.getById(dto.getId());
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }

        // 如果设为主要联系人，更新其他联系人状态
        if (dto.getIsPrimary() != null && dto.getIsPrimary() == 1) {
            clearPrimaryContact(dto.getCustomerId());
        }

        BeanUtils.copyProperties(dto, contact);
        this.updateById(contact);
    }

    @Override
    public void deleteContact(Long id) {
        log.info("删除联系人，联系人ID: {}", id);
        this.removeById(id);
    }

    @Override
    public void deleteContacts(List<Long> ids) {
        log.info("批量删除联系人，联系人ID列表: {}", ids);
        this.removeByIds(ids);
    }

    /**
     * 清除该客户下所有联系人的主要标记
     */
    private void clearPrimaryContact(Long customerId) {
        LambdaQueryWrapper<CustomerContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerContact::getCustomerId, customerId)
               .eq(CustomerContact::getIsPrimary, 1);

        List<CustomerContact> primaryContacts = this.list(wrapper);
        for (CustomerContact contact : primaryContacts) {
            contact.setIsPrimary(0);
        }
        this.updateBatchById(primaryContacts);
    }
}
