package com.emclims.module.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sample.entity.Sample;
import org.apache.ibatis.annotations.Mapper;

/**
 * 样品 Mapper
 */
@Mapper
public interface SampleMapper extends BaseMapper<Sample> {
}
