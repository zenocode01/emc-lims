package com.emclims.module.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.test.dto.TestPlanDTO;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.vo.TestPlanExportVO;
import com.emclims.module.test.vo.TestPlanVO;

/**
 * 测试计划 Service
 */
public interface TestPlanService extends IService<TestPlan> {

    /**
     * 分页查询测试计划
     */
    Page<TestPlanVO> pageTestPlans(Long sampleId, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取测试计划详情
     */
    TestPlanVO getTestPlanDetail(Long id);

    /**
     * 创建测试计划
     */
    void createTestPlan(TestPlanDTO dto);

    /**
     * 更新测试计划
     */
    void updateTestPlan(TestPlanDTO dto);

    /**
     * 删除测试计划
     */
    void deleteTestPlan(Long id);

    /**
     * 开始测试
     */
    void startTest(Long id);

    /**
     * 完成测试
     */
    void completeTest(Long id);

    /**
     * 导出测试计划列表
     */
    java.util.List<TestPlanExportVO> exportTestPlans(Long sampleId, String status);
}
