package com.emclims.module.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.test.entity.TestRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试记录 Mapper
 */
@Mapper
public interface TestRecordMapper extends BaseMapper<TestRecord> {
}
