package com.emclims.module.sample.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
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
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 样品 Service 实现
 */
@Service
public class SampleServiceImpl extends ServiceImpl<SampleMapper, Sample> implements SampleService {

    private final CustomerMapper customerMapper;
    private final SysUserMapper userMapper;
    private final SampleLogMapper sampleLogMapper;

    public SampleServiceImpl(CustomerMapper customerMapper, SysUserMapper userMapper,
                             SampleLogMapper sampleLogMapper) {
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.sampleLogMapper = sampleLogMapper;
    }

    @Override
    public Page<SampleVO> pageSamples(SampleQueryDTO queryDTO) {
        Page<Sample> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Sample> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), Sample::getSampleNo, queryDTO.getKeyword())
               .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), Sample::getProductName, queryDTO.getKeyword())
               .eq(queryDTO.getCustomerId() != null, Sample::getCustomerId, queryDTO.getCustomerId())
               .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Sample::getStatus, queryDTO.getStatus())
               .ge(queryDTO.getReceiveDateStart() != null, Sample::getReceiveDate, queryDTO.getReceiveDateStart())
               .le(queryDTO.getReceiveDateEnd() != null, Sample::getReceiveDate, queryDTO.getReceiveDateEnd())
               .orderByDesc(Sample::getCreateTime);

        Page<Sample> samplePage = this.page(page, wrapper);

        List<SampleVO> voList = samplePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());

        Page<SampleVO> result = new Page<>(samplePage.getCurrent(), samplePage.getSize(), samplePage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SampleVO getSampleDetail(Long id) {
        Sample sample = this.getById(id);
        if (sample == null) {
            throw new BusinessException("样品不存在");
        }
        return convertToVO(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveSample(SampleDTO dto) {
        // 生成样品编号（简化版：时间戳 + 随机数）
        String sampleNo = "S" + System.currentTimeMillis();

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
        this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(SampleStatusDTO dto) {
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

        return sampleLogMapper.selectList(wrapper).stream().map(log -> {
            SampleLogVO vo = new SampleLogVO();
            BeanUtils.copyProperties(log, vo);
            vo.setFromStatusName(log.getFromStatus() != null ? SampleStatusEnum.fromValue(log.getFromStatus()).getLabel() : "");
            vo.setToStatusName(SampleStatusEnum.fromValue(log.getToStatus()).getLabel());

            if (log.getOperator() != null) {
                SysUser user = userMapper.selectById(log.getOperator());
                if (user != null) {
                    vo.setOperatorName(user.getNickname());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为 VO
     */
    private SampleVO convertToVO(Sample sample) {
        SampleVO vo = new SampleVO();
        BeanUtils.copyProperties(sample, vo);
        vo.setStatusName(SampleStatusEnum.fromValue(sample.getStatus()).getLabel());

        if (sample.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(sample.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getName());
            }
        }
        if (sample.getTesterId() != null) {
            SysUser user = userMapper.selectById(sample.getTesterId());
            if (user != null) {
                vo.setTesterName(user.getNickname());
            }
        }
        if (sample.getReceiveBy() != null) {
            SysUser user = userMapper.selectById(sample.getReceiveBy());
            if (user != null) {
                vo.setReceiveByName(user.getNickname());
            }
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
