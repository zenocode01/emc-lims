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
import com.emclims.module.customer.vo.CustomerExportVO;
import com.emclims.module.customer.vo.CustomerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户 Service 实现
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public Page<CustomerVO> pageCustomers(CustomerQueryDTO queryDTO) {
        log.debug("查询客户列表，关键字: {}, 类型: {}, 行业: {}", queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getIndustry());
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Customer> wrapper = buildQueryWrapper(queryDTO);
        Page<Customer> customerPage = this.page(page, wrapper);

        List<CustomerVO> voList = customerPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<CustomerVO> result = new Page<>(customerPage.getCurrent(), customerPage.getSize(), customerPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public CustomerVO getCustomerDetail(Long id) {
        log.debug("获取客户详情，客户ID: {}", id);
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
        log.info("创建客户，客户名称: {}", dto.getName());
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
        log.info("更新客户信息，客户ID: {}, 客户名称: {}", dto.getId(), dto.getName());
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
        log.info("删除客户，客户ID列表: {}", ids);
        this.removeByIds(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("更新客户状态，客户ID: {}, 状态: {}", id, status);
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        customer.setStatus(status);
        this.updateById(customer);
    }

    @Override
    public List<CustomerExportVO> exportCustomers(CustomerQueryDTO queryDTO) {
        log.debug("导出客户列表，关键字: {}", queryDTO.getKeyword());
        LambdaQueryWrapper<Customer> wrapper = buildQueryWrapper(queryDTO);
        List<Customer> customers = this.list(wrapper);
        return customers.stream()
                .map(this::convertToExportVO)
                .collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Customer> buildQueryWrapper(CustomerQueryDTO queryDTO) {
        return new LambdaQueryWrapper<Customer>()
                .and(StrUtil.isNotBlank(queryDTO.getKeyword()), w ->
                        w.like(Customer::getName, queryDTO.getKeyword())
                                .or().like(Customer::getContact, queryDTO.getKeyword())
                                .or().like(Customer::getPhone, queryDTO.getKeyword()))
                .eq(queryDTO.getType() != null, Customer::getType, queryDTO.getType())
                .eq(StrUtil.isNotBlank(queryDTO.getIndustry()), Customer::getIndustry, queryDTO.getIndustry())
                .eq(queryDTO.getStatus() != null, Customer::getStatus, queryDTO.getStatus())
                .orderByDesc(Customer::getCreateTime);
    }

    /**
     * 转换为 VO
     */
    private CustomerVO convertToVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        vo.setTypeName(customer.getType() != null && customer.getType() == 1 ? "企业" : "个人");
        return vo;
    }

    /**
     * 转换为导出 VO
     */
    private CustomerExportVO convertToExportVO(Customer customer) {
        CustomerExportVO vo = new CustomerExportVO();
        BeanUtils.copyProperties(customer, vo);
        vo.setTypeName(customer.getType() != null && customer.getType() == 1 ? "企业" : "个人");
        vo.setStatusName(customer.getStatus() != null && customer.getStatus() == 1 ? "启用" : "禁用");
        return vo;
    }
}
