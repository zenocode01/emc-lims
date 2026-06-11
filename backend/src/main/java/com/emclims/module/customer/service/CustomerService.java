package com.emclims.module.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.customer.dto.CustomerDTO;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.vo.CustomerVO;

import java.util.List;

/**
 * 客户 Service
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 分页查询客户列表
     */
    Page<CustomerVO> pageCustomers(CustomerQueryDTO queryDTO);

    /**
     * 根据ID获取客户详情
     */
    CustomerVO getCustomerDetail(Long id);

    /**
     * 新增客户
     */
    void createCustomer(CustomerDTO dto);

    /**
     * 更新客户
     */
    void updateCustomer(CustomerDTO dto);

    /**
     * 批量删除客户
     */
    void deleteCustomers(List<Long> ids);

    /**
     * 修改客户状态
     */
    void updateStatus(Long id, Integer status);
}
