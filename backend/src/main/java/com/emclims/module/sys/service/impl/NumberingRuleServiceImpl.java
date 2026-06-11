package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysNumberingRule;
import com.emclims.module.sys.mapper.SysNumberingRuleMapper;
import com.emclims.module.sys.service.NumberingRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 编号规则服务实现
 */
@Service
public class NumberingRuleServiceImpl extends ServiceImpl<SysNumberingRuleMapper, SysNumberingRule>
        implements NumberingRuleService {

    @Override
    public List<SysNumberingRule> getRulesByModule(String moduleType) {
        return lambdaQuery()
                .eq(SysNumberingRule::getModuleType, moduleType)
                .eq(SysNumberingRule::getStatus, 1)
                .orderByAsc(SysNumberingRule::getCreateTime)
                .list();
    }

    @Override
    public void enableRule(Long id) {
        SysNumberingRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException("编号规则不存在: " + id);
        }
        rule.setStatus(1);
        this.updateById(rule);
    }

    @Override
    public void disableRule(Long id) {
        SysNumberingRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException("编号规则不存在: " + id);
        }
        rule.setStatus(0);
        this.updateById(rule);
    }
}
