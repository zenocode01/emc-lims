package com.emclims.module.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sample.entity.SampleLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 样品流转日志 Mapper
 */
@Mapper
public interface SampleLogMapper extends BaseMapper<SampleLog> {
}
