package com.emclims.module.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.test.entity.TestPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试计划 Mapper
 */
@Mapper
public interface TestPlanMapper extends BaseMapper<TestPlan> {
}
