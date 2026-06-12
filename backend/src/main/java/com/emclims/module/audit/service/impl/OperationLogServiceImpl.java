package com.emclims.module.audit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.response.PageResult;
import com.emclims.module.audit.dto.OperationLogQueryDTO;
import com.emclims.module.audit.entity.OperationLog;
import com.emclims.module.audit.mapper.OperationLogMapper;
import com.emclims.module.audit.service.OperationLogService;
import com.emclims.module.audit.vo.OperationLogVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 操作日志 Service 实现
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    private final SysUserMapper sysUserMapper;

    public OperationLogServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<OperationLogVO> pageLogs(OperationLogQueryDTO queryDTO) {
        log.debug("分页查询操作日志，模块：{}，操作：{}", queryDTO.getModule(), queryDTO.getAction());

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getOperatorId() != null, OperationLog::getOperatorId, queryDTO.getOperatorId())
               .eq(StrUtil.isNotBlank(queryDTO.getModule()), OperationLog::getModule, queryDTO.getModule())
               .eq(StrUtil.isNotBlank(queryDTO.getAction()), OperationLog::getAction, queryDTO.getAction())
               .ge(queryDTO.getStartTime() != null, OperationLog::getOperationTime, queryDTO.getStartTime())
               .le(queryDTO.getEndTime() != null, OperationLog::getOperationTime, queryDTO.getEndTime())
               .orderByDesc(OperationLog::getOperationTime);

        Page<OperationLog> page = this.page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        // 批量查询操作人信息，避免 N+1
        List<Long> operatorIds = page.getRecords().stream()
                .map(OperationLog::getOperatorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SysUser> userMap = operatorIds.isEmpty() ? Collections.emptyMap() :
                sysUserMapper.selectBatchIds(operatorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        // 转换为 VO
        List<OperationLogVO> voList = page.getRecords().stream().map(log -> {
            OperationLogVO vo = new OperationLogVO();
            BeanUtils.copyProperties(log, vo);
            // 填充操作人姓名
            if (log.getOperatorId() != null && userMap.containsKey(log.getOperatorId())) {
                vo.setOperatorName(userMap.get(log.getOperatorId()).getNickname());
            }
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), voList);
    }

    @Override
    public OperationLogVO getLogDetail(Long id) {
        log.debug("获取操作日志详情，日志 ID: {}", id);
        OperationLog log = this.getById(id);
        if (log == null) {
            throw new BusinessException("操作日志不存在");
        }

        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(log, vo);

        // 填充操作人姓名
        if (log.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(log.getOperatorId());
            if (user != null) {
                vo.setOperatorName(user.getNickname());
            }
        }

        return vo;
    }

    @Override
    public void deleteLogs(Long[] ids) {
        log.info("批量删除操作日志，日志 ID 列表: {}", ids);
        this.removeByIds(List.of(ids));
    }
}
