package com.emclims.module.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户 Mapper
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
