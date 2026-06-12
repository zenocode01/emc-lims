package com.emclims.module.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.test.dto.TestItemDTO;
import com.emclims.module.test.dto.TestItemQueryDTO;
import com.emclims.module.test.entity.TestItem;
import com.emclims.module.test.vo.TestItemVO;

/**
 * 测试项目 Service
 */
public interface TestItemService extends IService<TestItem> {

    /**
     * 分页查询测试项目
     */
    Page<TestItemVO> pageTestItems(TestItemQueryDTO queryDTO);

    /**
     * 获取测试项目详情
     */
    TestItemVO getTestItemDetail(Long id);

    /**
     * 新增测试项目
     */
    void createTestItem(TestItemDTO dto);

    /**
     * 更新测试项目
     */
    void updateTestItem(TestItemDTO dto);

    /**
     * 删除测试项目
     */
    void deleteTestItem(Long id);

    /**
     * 修改测试项目状态
     */
    void updateTestItemStatus(Long id, Integer status);
}
