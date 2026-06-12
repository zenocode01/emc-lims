package com.emclims.module.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.test.entity.TestItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试项目 Mapper
 */
@Mapper
public interface TestItemMapper extends BaseMapper<TestItem> {
}
