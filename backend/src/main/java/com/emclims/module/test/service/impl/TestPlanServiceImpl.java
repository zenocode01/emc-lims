package com.emclims.module.test.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import com.emclims.module.test.dto.TestPlanDTO;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.mapper.TestPlanMapper;
import com.emclims.module.test.service.TestPlanService;
import com.emclims.module.test.vo.TestPlanExportVO;
import com.emclims.module.test.vo.TestPlanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试计划 Service 实现
 */
@Slf4j
@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {

    @Autowired
    SampleMapper sampleMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public Page<TestPlanVO> pageTestPlans(Long sampleId, String status, Integer pageNum, Integer pageSize) {
        log.debug("分页查询测试计划，样品ID: {}, 状态: {}", sampleId, status);

        LambdaQueryWrapper<TestPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(sampleId != null, TestPlan::getSampleId, sampleId)
               .eq(StrUtil.isNotBlank(status), TestPlan::getStatus, status)
               .orderByDesc(TestPlan::getCreateTime);

        Page<TestPlan> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        // 批量查询样品和客户信息，避免 N+1
        List<Long> sampleIds = page.getRecords().stream().map(TestPlan::getSampleId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> customerIds = page.getRecords().stream().map(TestPlan::getCustomerId).filter(id -> id != null).distinct().collect(Collectors.toList());

        Map<Long, Sample> sampleMap = sampleIds.isEmpty() ? Collections.emptyMap() :
                sampleMapper.selectBatchIds(sampleIds).stream().collect(Collectors.toMap(Sample::getId, s -> s));
        Map<Long, Customer> customerMap = customerIds.isEmpty() ? Collections.emptyMap() :
                customerMapper.selectBatchIds(customerIds).stream().collect(Collectors.toMap(Customer::getId, c -> c));

        Page<TestPlanVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(p -> convertToVO(p, sampleMap, customerMap)).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public TestPlanVO getTestPlanDetail(Long id) {
        log.debug("获取测试计划详情，ID: {}", id);
        TestPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("测试计划不存在");
        }

        // 查询样品和客户信息
        Sample sample = plan.getSampleId() != null ? sampleMapper.selectById(plan.getSampleId()) : null;
        Customer customer = plan.getCustomerId() != null ? customerMapper.selectById(plan.getCustomerId()) : null;

        return convertToVO(plan,
                sample != null ? Collections.singletonMap(sample.getId(), sample) : Collections.emptyMap(),
                customer != null ? Collections.singletonMap(customer.getId(), customer) : Collections.emptyMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTestPlan(TestPlanDTO dto) {
        log.info("创建测试计划，样品ID: {}, 计划日期: {}", dto.getSampleId(), dto.getPlanDate());
        TestPlan plan = new TestPlan();
        BeanUtils.copyProperties(dto, plan);
        // 生成计划编号
        plan.setPlanNo(generatePlanNo());
        plan.setStatus("draft");
        this.save(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTestPlan(TestPlanDTO dto) {
        log.info("更新测试计划，ID: {}", dto.getId());
        TestPlan plan = this.getById(dto.getId());
        if (plan == null) {
            throw new BusinessException("测试计划不存在");
        }
        BeanUtils.copyProperties(dto, plan, "id", "createTime");
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTestPlan(Long id) {
        log.info("删除测试计划，ID: {}", id);
        TestPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("测试计划不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTest(Long id) {
        log.info("开始测试，测试计划ID: {}", id);
        TestPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("测试计划不存在");
        }
        if (!"draft".equals(plan.getStatus())) {
            throw new BusinessException("只有草稿状态的测试计划可以开始测试");
        }
        plan.setStatus("testing");
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTest(Long id) {
        log.info("完成测试，测试计划ID: {}", id);
        TestPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("测试计划不存在");
        }
        if (!"testing".equals(plan.getStatus())) {
            throw new BusinessException("只有测试中的测试计划可以完成");
        }
        plan.setStatus("completed");
        this.updateById(plan);
    }

    @Override
    public java.util.List<TestPlanExportVO> exportTestPlans(Long sampleId, String status) {
        log.debug("导出测试计划，样品ID: {}, 状态: {}", sampleId, status);

        LambdaQueryWrapper<TestPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(sampleId != null, TestPlan::getSampleId, sampleId)
               .eq(StrUtil.isNotBlank(status), TestPlan::getStatus, status)
               .orderByDesc(TestPlan::getCreateTime);

        List<TestPlan> plans = this.list(wrapper);
        if (plans.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询样品、客户信息
        List<Long> sampleIds = plans.stream().map(TestPlan::getSampleId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> customerIds = plans.stream().map(TestPlan::getCustomerId).filter(id -> id != null).distinct().collect(Collectors.toList());

        Map<Long, Sample> sampleMap = sampleIds.isEmpty() ? Collections.emptyMap() :
                sampleMapper.selectBatchIds(sampleIds).stream().collect(Collectors.toMap(Sample::getId, s -> s));
        Map<Long, Customer> customerMap = customerIds.isEmpty() ? Collections.emptyMap() :
                customerMapper.selectBatchIds(customerIds).stream().collect(Collectors.toMap(Customer::getId, c -> c));

        // 转换为导出 VO
        return plans.stream().map(plan -> {
            TestPlanExportVO vo = new TestPlanExportVO();
            BeanUtils.copyProperties(plan, vo);

            // 填充样品编号
            if (plan.getSampleId() != null && sampleMap.containsKey(plan.getSampleId())) {
                Sample sample = sampleMap.get(plan.getSampleId());
                vo.setSampleNo(sample.getSampleNo());
            }

            // 填充客户名称
            if (plan.getCustomerId() != null && customerMap.containsKey(plan.getCustomerId())) {
                Customer customer = customerMap.get(plan.getCustomerId());
                vo.setCustomerName(customer.getName());
            }

            // 填充状态名称
            vo.setStatusName(convertStatusName(plan.getStatus()));

            // 测试项目转 JSON 字符串
            if (plan.getTestItems() != null) {
                vo.setTestItems(plan.getTestItems());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为 VO
     */
    private TestPlanVO convertToVO(TestPlan plan, Map<Long, Sample> sampleMap, Map<Long, Customer> customerMap) {
        TestPlanVO vo = new TestPlanVO();
        BeanUtils.copyProperties(plan, vo);

        // 填充样品名称
        if (plan.getSampleId() != null && sampleMap.containsKey(plan.getSampleId())) {
            Sample sample = sampleMap.get(plan.getSampleId());
            vo.setSampleNo(sample.getSampleNo());
            vo.setProductName(sample.getProductName());
        }

        // 填充客户名称
        if (plan.getCustomerId() != null && customerMap.containsKey(plan.getCustomerId())) {
            Customer customer = customerMap.get(plan.getCustomerId());
            vo.setCustomerName(customer.getName());
        }

        vo.setStatusName(convertStatusName(plan.getStatus()));
        return vo;
    }

    /**
     * 转换状态名称
     */
    private String convertStatusName(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "draft":
                return "草稿";
            case "testing":
                return "测试中";
            case "completed":
                return "已完成";
            case "cancelled":
                return "已取消";
            default:
                return status;
        }
    }

    /**
     * 生成测试计划编号
     * 格式：TP-yyyyMMdd-xxxx
     */
    private String generatePlanNo() {
        String dateStr = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 简单随机后缀，实际项目中可使用编号规则引擎
        String suffix = String.format("%04d", (int) (Math.random() * 10000));
        return "TP-" + dateStr + "-" + suffix;
    }
}
