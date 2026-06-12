package com.emclims.module.test.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.test.dto.TestRecordDTO;
import com.emclims.module.test.dto.TestRecordQueryDTO;
import com.emclims.module.test.entity.TestItem;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.entity.TestRecord;
import com.emclims.module.test.mapper.TestItemMapper;
import com.emclims.module.test.mapper.TestPlanMapper;
import com.emclims.module.test.mapper.TestRecordMapper;
import com.emclims.module.test.service.TestRecordService;
import com.emclims.module.test.vo.TestRecordExportVO;
import com.emclims.module.test.vo.TestRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试记录 Service 实现
 */
@Slf4j
@Service
public class TestRecordServiceImpl extends ServiceImpl<TestRecordMapper, TestRecord> implements TestRecordService {

    @Autowired
    SampleMapper sampleMapper;

    @Autowired
    TestPlanMapper testPlanMapper;

    @Autowired
    TestItemMapper testItemMapper;

    @Autowired
    SysUserMapper userMapper;

    @Override
    public Page<TestRecordVO> pageTestRecords(TestRecordQueryDTO queryDTO) {
        log.debug("分页查询测试记录，计划ID: {}, 项目ID: {}, 状态: {}", queryDTO.getTestPlanId(), queryDTO.getTestItemId(), queryDTO.getResult());

        LambdaQueryWrapper<TestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getTestPlanId() != null, TestRecord::getTestPlanId, queryDTO.getTestPlanId())
               .eq(queryDTO.getTestItemId() != null, TestRecord::getTestItemId, queryDTO.getTestItemId())
               .eq(queryDTO.getTesterId() != null, TestRecord::getTesterId, queryDTO.getTesterId())
               .eq(StrUtil.isNotBlank(queryDTO.getResult()), TestRecord::getResult, queryDTO.getResult())
               .ge(queryDTO.getStartDate() != null, TestRecord::getTestDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, TestRecord::getTestDate, queryDTO.getEndDate())
               .orderByDesc(TestRecord::getTestDate);

        Page<TestRecord> page = this.page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        // 批量查询关联信息，避免 N+1
        List<Long> testPlanIds = page.getRecords().stream().map(TestRecord::getTestPlanId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> testItemIds = page.getRecords().stream().map(TestRecord::getTestItemId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> testerIds = page.getRecords().stream().map(TestRecord::getTesterId).filter(id -> id != null).distinct().collect(Collectors.toList());

        // 批量查询测试计划和测试项目信息
        Map<Long, TestPlan> testPlanMap = testPlanIds.isEmpty() ? Collections.emptyMap() :
                testPlanMapper.selectBatchIds(testPlanIds).stream().collect(Collectors.toMap(TestPlan::getId, p -> p));
        Map<Long, TestItem> testItemMap = testItemIds.isEmpty() ? Collections.emptyMap() :
                testItemMapper.selectBatchIds(testItemIds).stream().collect(Collectors.toMap(TestItem::getId, t -> t));
        Map<Long, SysUser> userMap = testerIds.isEmpty() ? Collections.emptyMap() :
                userMapper.selectBatchIds(testerIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        // 构建测试计划到样品的映射
        Map<Long, Sample> sampleMap = testPlanIds.isEmpty() ? Collections.emptyMap() :
                sampleMapper.selectBatchIds(testPlanMap.values().stream().map(TestPlan::getSampleId).filter(id -> id != null).distinct().collect(Collectors.toList()))
                        .stream().collect(Collectors.toMap(Sample::getId, s -> s));

        Page<TestRecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(r -> convertToVO(r, testPlanMap, sampleMap, testItemMap, userMap)).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public TestRecordVO getTestRecordDetail(Long id) {
        log.debug("获取测试记录详情，ID: {}", id);
        TestRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException("测试记录不存在");
        }

        // 查询关联信息
        TestPlan testPlan = record.getTestPlanId() != null ? testPlanMapper.selectById(record.getTestPlanId()) : null;
        Sample sample = testPlan != null && testPlan.getSampleId() != null ? sampleMapper.selectById(testPlan.getSampleId()) : null;
        TestItem testItem = record.getTestItemId() != null ? testItemMapper.selectById(record.getTestItemId()) : null;
        SysUser tester = record.getTesterId() != null ? userMapper.selectById(record.getTesterId()) : null;

        return convertToVO(record,
                testPlan != null ? Collections.singletonMap(testPlan.getId(), testPlan) : Collections.emptyMap(),
                sample != null ? Collections.singletonMap(sample.getId(), sample) : Collections.emptyMap(),
                testItem != null ? Collections.singletonMap(testItem.getId(), testItem) : Collections.emptyMap(),
                tester != null ? Collections.singletonMap(tester.getId(), tester) : Collections.emptyMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTestRecord(TestRecordDTO dto) {
        log.info("新增测试记录，计划ID: {}, 项目ID: {}, 结果: {}", dto.getTestPlanId(), dto.getTestItemId(), dto.getResult());
        TestRecord record = new TestRecord();
        BeanUtils.copyProperties(dto, record);
        this.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTestRecord(TestRecordDTO dto) {
        log.info("更新测试记录，ID: {}", dto.getId());
        TestRecord record = this.getById(dto.getId());
        if (record == null) {
            throw new BusinessException("测试记录不存在");
        }
        BeanUtils.copyProperties(dto, record, "id", "createTime");
        this.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTestRecord(Long id) {
        log.info("删除测试记录，ID: {}", id);
        TestRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException("测试记录不存在");
        }
        this.removeById(id);
    }

    /**
     * 转换为 VO
     */
    private TestRecordVO convertToVO(TestRecord record,
                                      Map<Long, TestPlan> testPlanMap,
                                      Map<Long, Sample> sampleMap,
                                      Map<Long, TestItem> testItemMap,
                                      Map<Long, SysUser> userMap) {
        TestRecordVO vo = new TestRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 填充测试计划编号和样品信息
        if (record.getTestPlanId() != null && testPlanMap.containsKey(record.getTestPlanId())) {
            TestPlan testPlan = testPlanMap.get(record.getTestPlanId());
            vo.setPlanNo(testPlan.getPlanNo());
            if (testPlan.getSampleId() != null && sampleMap.containsKey(testPlan.getSampleId())) {
                Sample sample = sampleMap.get(testPlan.getSampleId());
                vo.setSampleNo(sample.getSampleNo());
                vo.setProductName(sample.getProductName());
            }
        }

        // 填充测试项目信息
        if (record.getTestItemId() != null && testItemMap.containsKey(record.getTestItemId())) {
            TestItem testItem = testItemMap.get(record.getTestItemId());
            vo.setTestItemCode(testItem.getCode());
            vo.setTestItemName(testItem.getName());
            vo.setTestItemCategory(testItem.getCategory());
        }

        // 填充测试人员名称
        if (record.getTesterId() != null && userMap.containsKey(record.getTesterId())) {
            SysUser user = userMap.get(record.getTesterId());
            vo.setTesterName(user.getNickname());
        }

        // 填充结果名称
        vo.setResultName(convertResultName(record.getResult()));
        return vo;
    }

    @Override
    public java.util.List<TestRecordExportVO> exportTestRecords(TestRecordQueryDTO queryDTO) {
        log.debug("导出测试记录，计划ID: {}, 项目ID: {}, 状态: {}", queryDTO.getTestPlanId(), queryDTO.getTestItemId(), queryDTO.getResult());

        LambdaQueryWrapper<TestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getTestPlanId() != null, TestRecord::getTestPlanId, queryDTO.getTestPlanId())
               .eq(queryDTO.getTestItemId() != null, TestRecord::getTestItemId, queryDTO.getTestItemId())
               .eq(queryDTO.getTesterId() != null, TestRecord::getTesterId, queryDTO.getTesterId())
               .eq(StrUtil.isNotBlank(queryDTO.getResult()), TestRecord::getResult, queryDTO.getResult())
               .ge(queryDTO.getStartDate() != null, TestRecord::getTestDate, queryDTO.getStartDate())
               .le(queryDTO.getEndDate() != null, TestRecord::getTestDate, queryDTO.getEndDate())
               .orderByDesc(TestRecord::getTestDate);

        List<TestRecord> records = this.list(wrapper);

        // 批量查询关联信息
        List<Long> testPlanIds = records.stream().map(TestRecord::getTestPlanId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> testItemIds = records.stream().map(TestRecord::getTestItemId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> testerIds = records.stream().map(TestRecord::getTesterId).filter(id -> id != null).distinct().collect(Collectors.toList());

        Map<Long, TestPlan> testPlanMap = testPlanIds.isEmpty() ? Collections.emptyMap() :
                testPlanMapper.selectBatchIds(testPlanIds).stream().collect(Collectors.toMap(TestPlan::getId, p -> p));
        Map<Long, TestItem> testItemMap = testItemIds.isEmpty() ? Collections.emptyMap() :
                testItemMapper.selectBatchIds(testItemIds).stream().collect(Collectors.toMap(TestItem::getId, t -> t));
        Map<Long, SysUser> userMap = testerIds.isEmpty() ? Collections.emptyMap() :
                userMapper.selectBatchIds(testerIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        Map<Long, Sample> sampleMap = testPlanIds.isEmpty() ? Collections.emptyMap() :
                sampleMapper.selectBatchIds(testPlanMap.values().stream().map(TestPlan::getSampleId).filter(id -> id != null).distinct().collect(Collectors.toList()))
                        .stream().collect(Collectors.toMap(Sample::getId, s -> s));

        return records.stream()
                .map(r -> convertToExportVO(r, testPlanMap, sampleMap, testItemMap, userMap))
                .collect(Collectors.toList());
    }

    /**
     * 转换为导出 VO
     */
    private TestRecordExportVO convertToExportVO(TestRecord record,
                                                  Map<Long, TestPlan> testPlanMap,
                                                  Map<Long, Sample> sampleMap,
                                                  Map<Long, TestItem> testItemMap,
                                                  Map<Long, SysUser> userMap) {
        TestRecordExportVO vo = new TestRecordExportVO();
        BeanUtils.copyProperties(record, vo);

        // 填充计划编号和样品编号
        if (record.getTestPlanId() != null && testPlanMap.containsKey(record.getTestPlanId())) {
            TestPlan testPlan = testPlanMap.get(record.getTestPlanId());
            vo.setPlanNo(testPlan.getPlanNo());
            if (testPlan.getSampleId() != null && sampleMap.containsKey(testPlan.getSampleId())) {
                Sample sample = sampleMap.get(testPlan.getSampleId());
                vo.setSampleNo(sample.getSampleNo());
            }
        }

        // 填充测试项目信息
        if (record.getTestItemId() != null && testItemMap.containsKey(record.getTestItemId())) {
            TestItem testItem = testItemMap.get(record.getTestItemId());
            vo.setTestItemCode(testItem.getCode());
            vo.setTestItemName(testItem.getName());
        }

        // 填充测试人员名称
        if (record.getTesterId() != null && userMap.containsKey(record.getTesterId())) {
            SysUser user = userMap.get(record.getTesterId());
            vo.setTesterName(user.getNickname());
        }

        // 填充结果名称
        vo.setResultName(convertResultName(record.getResult()));
        return vo;
    }

    /**
     * 转换结果名称
     */
    private String convertResultName(String result) {
        if (result == null) {
            return "";
        }
        switch (result) {
            case "pass":
                return "通过";
            case "fail":
                return "不通过";
            case "na":
                return "不适用";
            default:
                return result;
        }
    }
}
