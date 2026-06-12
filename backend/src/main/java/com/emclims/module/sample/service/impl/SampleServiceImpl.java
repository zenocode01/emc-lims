package com.emclims.module.sample.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.numbering.NumberingRuleEngine;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.sample.dto.SampleDTO;
import com.emclims.module.sample.dto.SampleQueryDTO;
import com.emclims.module.sample.dto.SampleStatusDTO;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.entity.SampleLog;
import com.emclims.module.sample.enums.SampleStatusEnum;
import com.emclims.module.sample.mapper.SampleLogMapper;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sample.service.SampleService;
import com.emclims.module.sample.vo.SampleExportVO;
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 样品 Service 实现
 */
@Slf4j
@Service
public class SampleServiceImpl extends ServiceImpl<SampleMapper, Sample> implements SampleService {

    private final CustomerMapper customerMapper;
    private final SysUserMapper userMapper;
    private final SampleLogMapper sampleLogMapper;
    private final NumberingRuleEngine numberingRuleEngine;

    public SampleServiceImpl(CustomerMapper customerMapper, SysUserMapper userMapper,
                             SampleLogMapper sampleLogMapper,
                             NumberingRuleEngine numberingRuleEngine) {
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.sampleLogMapper = sampleLogMapper;
        this.numberingRuleEngine = numberingRuleEngine;
    }

    @Override
    public Page<SampleVO> pageSamples(SampleQueryDTO queryDTO) {
        log.debug("查询样品列表，关键字: {}, 客户ID: {}, 状态: {}", queryDTO.getKeyword(), queryDTO.getCustomerId(), queryDTO.getStatus());
        Page<Sample> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Sample> wrapper = buildQueryWrapper(queryDTO);
        Page<Sample> samplePage = this.page(page, wrapper);

        // 批量查询客户和测试员，避免 N+1
        List<Long> customerIds = samplePage.getRecords().stream().map(Sample::getCustomerId).filter(id -> id != null).distinct().collect(Collectors.toList());
        java.util.Set<Long> userIdSet = new java.util.HashSet<>();
        for (Sample s : samplePage.getRecords()) {
            if (s.getTesterId() != null) userIdSet.add(s.getTesterId());
            if (s.getReceiveBy() != null) userIdSet.add(s.getReceiveBy());
        }
        java.util.List<Long> allUserIds = new java.util.ArrayList<>(userIdSet);

        java.util.Map<Long, Customer> customerMap = customerIds.isEmpty() ? java.util.Collections.emptyMap() :
                customerMapper.selectBatchIds(customerIds).stream().collect(Collectors.toMap(Customer::getId, c -> c));
        java.util.Map<Long, SysUser> userMap = allUserIds.isEmpty() ? java.util.Collections.emptyMap() :
                userMapper.selectBatchIds(allUserIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        List<SampleVO> voList = samplePage.getRecords().stream().map(s -> convertToVO(s, customerMap, userMap)).collect(Collectors.toList());

        Page<SampleVO> result = new Page<>(samplePage.getCurrent(), samplePage.getSize(), samplePage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SampleVO getSampleDetail(Long id) {
        log.debug("获取样品详情，样品ID: {}", id);
        Sample sample = this.getById(id);
        if (sample == null) {
            throw new BusinessException("样品不存在");
        }
        return convertToVO(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveSample(SampleDTO dto) {
        log.info("收样登记，客户ID: {}, 产品名称: {}", dto.getCustomerId(), dto.getProductName());
        // 使用编号规则引擎生成样品编号（格式：EMC-yyyyMMdd-xxxx）
        String sampleNo = numberingRuleEngine.generateNumber("SAMPLE_DEFAULT");

        Sample sample = new Sample();
        BeanUtils.copyProperties(dto, sample);
        sample.setSampleNo(sampleNo);
        sample.setStatus(SampleStatusEnum.RECEIVED.getValue());
        this.save(sample);

        // 记录流转日志
        createLog(sample.getId(), null, SampleStatusEnum.RECEIVED.getValue(), "收样登记");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSample(SampleDTO dto) {
        log.info("更新样品信息，样品ID: {}", dto.getId());
        Sample sample = this.getById(dto.getId());
        if (sample == null) {
            throw new BusinessException("样品不存在");
        }
        BeanUtils.copyProperties(dto, sample);
        this.updateById(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSamples(List<Long> ids) {
        log.info("删除样品，样品ID列表: {}", ids);
        this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(SampleStatusDTO dto) {
        log.info("变更样品状态，样品ID: {}, 目标状态: {}", dto.getSampleId(), dto.getToStatus());
        Sample sample = this.getById(dto.getSampleId());
        if (sample == null) {
            throw new BusinessException("样品不存在");
        }

        String fromStatus = sample.getStatus();
        sample.setStatus(dto.getToStatus());
        this.updateById(sample);

        // 记录流转日志
        createLog(dto.getSampleId(), fromStatus, dto.getToStatus(), dto.getRemark());
    }

    @Override
    public List<SampleLogVO> getSampleLogs(Long sampleId) {
        LambdaQueryWrapper<SampleLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SampleLog::getSampleId, sampleId)
               .orderByDesc(SampleLog::getOperateTime);

        java.util.List<SampleLog> logs = sampleLogMapper.selectList(wrapper);

        // 批量查询操作员，避免 N+1
        java.util.List<Long> operatorIds = logs.stream().map(SampleLog::getOperator).filter(id -> id != null).distinct().collect(Collectors.toList());
        java.util.Map<Long, SysUser> userMap = operatorIds.isEmpty() ? java.util.Collections.emptyMap() :
                userMapper.selectBatchIds(operatorIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        return logs.stream().map(log -> {
            SampleLogVO vo = new SampleLogVO();
            BeanUtils.copyProperties(log, vo);
            vo.setFromStatusName(log.getFromStatus() != null ? SampleStatusEnum.fromValue(log.getFromStatus()).getLabel() : "");
            vo.setToStatusName(SampleStatusEnum.fromValue(log.getToStatus()).getLabel());

            if (log.getOperator() != null && userMap.containsKey(log.getOperator())) {
                vo.setOperatorName(userMap.get(log.getOperator()).getNickname());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Sample> buildQueryWrapper(SampleQueryDTO queryDTO) {
        return new LambdaQueryWrapper<Sample>()
                .and(StrUtil.isNotBlank(queryDTO.getKeyword()), w -> w.like(Sample::getSampleNo, queryDTO.getKeyword())
                       .or().like(Sample::getProductName, queryDTO.getKeyword()))
                .eq(queryDTO.getCustomerId() != null, Sample::getCustomerId, queryDTO.getCustomerId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Sample::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getReceiveDateStart() != null, Sample::getReceiveDate, queryDTO.getReceiveDateStart())
                .le(queryDTO.getReceiveDateEnd() != null, Sample::getReceiveDate, queryDTO.getReceiveDateEnd())
                .orderByDesc(Sample::getCreateTime);
    }

    @Override
    public List<SampleExportVO> exportSamples(SampleQueryDTO queryDTO) {
        log.debug("导出样品列表，关键字: {}, 客户ID: {}, 状态: {}", queryDTO.getKeyword(), queryDTO.getCustomerId(), queryDTO.getStatus());
        LambdaQueryWrapper<Sample> wrapper = buildQueryWrapper(queryDTO);
        List<Sample> samples = this.list(wrapper);

        // 批量查询客户和测试员，避免 N+1
        List<Long> customerIds = samples.stream().map(Sample::getCustomerId).filter(id -> id != null).distinct().collect(Collectors.toList());
        Set<Long> userIdSet = new HashSet<>();
        for (Sample s : samples) {
            if (s.getTesterId() != null) userIdSet.add(s.getTesterId());
            if (s.getReceiveBy() != null) userIdSet.add(s.getReceiveBy());
        }
        List<Long> allUserIds = new ArrayList<>(userIdSet);

        Map<Long, Customer> customerMap = customerIds.isEmpty() ? Collections.emptyMap() :
                customerMapper.selectBatchIds(customerIds).stream().collect(Collectors.toMap(Customer::getId, c -> c));
        Map<Long, SysUser> userMap = allUserIds.isEmpty() ? Collections.emptyMap() :
                userMapper.selectBatchIds(allUserIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        return samples.stream().map(s -> convertToExportVO(s, customerMap, userMap)).collect(Collectors.toList());
    }

    /**
     * 转换为导出 VO
     */
    private SampleExportVO convertToExportVO(Sample sample, Map<Long, Customer> customerMap, Map<Long, SysUser> userMap) {
        SampleExportVO vo = new SampleExportVO();
        BeanUtils.copyProperties(sample, vo);
        vo.setStatusName(SampleStatusEnum.fromValue(sample.getStatus()).getLabel());
        vo.setCustomerName(getCustomerName(sample, customerMap));
        vo.setTesterName(getUserNickname(sample, sample.getTesterId(), userMap));
        vo.setReceiveByName(getUserNickname(sample, sample.getReceiveBy(), userMap));
        return vo;
    }

    /**
     * 从 Map 中安全获取客户名称
     */
    private String getCustomerName(Sample sample, java.util.Map<Long, Customer> customerMap) {
        if (sample.getCustomerId() == null || customerMap == null) {
            return null;
        }
        Customer customer = customerMap.get(sample.getCustomerId());
        return customer != null ? customer.getName() : null;
    }

    /**
     * 从 Map 中安全获取用户昵称
     */
    private String getUserNickname(Sample sample, Long userId, java.util.Map<Long, SysUser> userMap) {
        if (userId == null || userMap == null) {
            return null;
        }
        SysUser user = userMap.get(userId);
        return user != null ? user.getNickname() : null;
    }

    /**
     * 转换为 VO（批量查询版）
     */
    private SampleVO convertToVO(Sample sample, java.util.Map<Long, Customer> customerMap, java.util.Map<Long, SysUser> userMap) {
        SampleVO vo = new SampleVO();
        BeanUtils.copyProperties(sample, vo);
        vo.setStatusName(SampleStatusEnum.fromValue(sample.getStatus()).getLabel());
        vo.setCustomerName(getCustomerName(sample, customerMap));
        vo.setTesterName(getUserNickname(sample, sample.getTesterId(), userMap));
        vo.setReceiveByName(getUserNickname(sample, sample.getReceiveBy(), userMap));
        return vo;
    }

    /**
     * 转换为 VO（单个查询，用于详情页面）
     */
    private SampleVO convertToVO(Sample sample) {
        SampleVO vo = new SampleVO();
        BeanUtils.copyProperties(sample, vo);
        vo.setStatusName(SampleStatusEnum.fromValue(sample.getStatus()).getLabel());
        if (sample.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(sample.getCustomerId());
            vo.setCustomerName(customer != null ? customer.getName() : null);
        }
        if (sample.getTesterId() != null) {
            SysUser user = userMapper.selectById(sample.getTesterId());
            vo.setTesterName(user != null ? user.getNickname() : null);
        }
        if (sample.getReceiveBy() != null) {
            SysUser user = userMapper.selectById(sample.getReceiveBy());
            vo.setReceiveByName(user != null ? user.getNickname() : null);
        }
        return vo;
    }

    /**
     * 创建流转日志
     */
    private void createLog(Long sampleId, String fromStatus, String toStatus, String remark) {
        SampleLog log = new SampleLog();
        log.setSampleId(sampleId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperateTime(LocalDateTime.now());
        log.setRemark(remark);
        // 从安全上下文获取当前用户
        log.setOperator(com.emclims.common.security.SecurityUtils.getCurrentUserId());
        sampleLogMapper.insert(log);
    }
}
