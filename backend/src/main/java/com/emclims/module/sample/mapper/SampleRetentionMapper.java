package com.emclims.module.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sample.entity.SampleRetention;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留样记录 Mapper
 */
@Mapper
public interface SampleRetentionMapper extends BaseMapper<SampleRetention> {
}
