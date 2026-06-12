package com.emclims.module.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.test.dto.TestRecordDTO;
import com.emclims.module.test.dto.TestRecordQueryDTO;
import com.emclims.module.test.entity.TestRecord;
import com.emclims.module.test.vo.TestRecordExportVO;
import com.emclims.module.test.vo.TestRecordVO;

import java.util.List;

/**
 * 测试记录 Service
 */
public interface TestRecordService extends IService<TestRecord> {

    /**
     * 分页查询测试记录
     */
    Page<TestRecordVO> pageTestRecords(TestRecordQueryDTO queryDTO);

    /**
     * 获取测试记录详情
     */
    TestRecordVO getTestRecordDetail(Long id);

    /**
     * 新增测试记录
     */
    void createTestRecord(TestRecordDTO dto);

    /**
     * 更新测试记录
     */
    void updateTestRecord(TestRecordDTO dto);

    /**
     * 删除测试记录
     */
    void deleteTestRecord(Long id);

    /**
     * 导出测试记录列表
     */
    List<TestRecordExportVO> exportTestRecords(TestRecordQueryDTO queryDTO);
}
