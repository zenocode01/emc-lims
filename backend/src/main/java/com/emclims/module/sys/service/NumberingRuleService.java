package com.emclims.module.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sys.entity.SysNumberingRule;

import java.util.List;

/**
 * 编号规则服务接口
 */
public interface NumberingRuleService extends IService<SysNumberingRule> {

    /**
     * 根据模块类型获取启用的规则列表
     */
    List<SysNumberingRule> getRulesByModule(String moduleType);

    /**
     * 启用规则
     */
    void enableRule(Long id);

    /**
     * 禁用规则
     */
    void disableRule(Long id);
}
