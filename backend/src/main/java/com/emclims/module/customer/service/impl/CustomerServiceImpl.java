package com.emclims.module.customer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.dto.CustomerDTO;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.customer.service.CustomerService;
import com.emclims.module.customer.vo.CustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户 Service 实现
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public Page<CustomerVO> pageCustomers(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), Customer::getName, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), Customer::getContact, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), Customer::getPhone, queryDTO.getKeyword())
               .eq(queryDTO.getType() != null, Customer::getType, queryDTO.getType())
               .eq(StrUtil.isNotBlank(queryDTO.getIndustry()), Customer::getIndustry, queryDTO.getIndustry())
               .eq(queryDTO.getStatus() != null, Customer::getStatus, queryDTO.getStatus())
               .orderByDesc(Customer::getCreateTime);

        Page<Customer> customerPage = this.page(page, wrapper);

        List<CustomerVO> voList = customerPage.getRecords().stream().map(customer -> {
            CustomerVO vo = new CustomerVO();
            BeanUtils.copyProperties(customer, vo);
            vo.setTypeName(customer.getType() != null && customer.getType() == 1 ? "企业" : "个人");
            return vo;
        }).collect(Collectors.toList());

        Page<CustomerVO> result = new Page<>(customerPage.getCurrent(), customerPage.getSize(), customerPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public CustomerVO getCustomerDetail(Long id) {
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        vo.setTypeName(customer.getType() != null && customer.getType() == 1 ? "企业" : "个人");
        return vo;
    }

    @Override
    public void createCustomer(CustomerDTO dto) {
        // 检查客户名称是否已存在
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getName, dto.getName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("客户名称已存在");
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        this.save(customer);
    }

    @Override
    public void updateCustomer(CustomerDTO dto) {
        Customer customer = this.getById(dto.getId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 检查客户名称是否被其他客户使用
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getName, dto.getName())
               .ne(Customer::getId, dto.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("客户名称已被其他客户使用");
        }

        BeanUtils.copyProperties(dto, customer);
        this.updateById(customer);
    }

    @Override
    public void deleteCustomers(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        customer.setStatus(status);
        this.updateById(customer);
    }
}
