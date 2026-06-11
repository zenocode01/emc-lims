package com.emclims.common.numbering;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysNumberingRule;
import com.emclims.module.sys.entity.SysNumberingSequence;
import com.emclims.module.sys.mapper.SysNumberingRuleMapper;
import com.emclims.module.sys.mapper.SysNumberingSequenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 编号规则引擎
 * <p>
 * 根据配置的编号规则自动生成业务编号，格式：{prefix}{separator}{date}{separator}{seq}
 * 示例：EMC-20260611-0001
 * <p>
 * 通过数据库行锁（SELECT ... FOR UPDATE）保证并发安全，同一规则同一天内序列号不重复。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NumberingRuleEngine {

    private final SysNumberingRuleMapper ruleMapper;
    private final SysNumberingSequenceMapper sequenceMapper;

    /**
     * 根据规则编码生成下一个编号
     *
     * @param ruleCode 规则编码
     * @return 生成的编号字符串
     */
    public String generateNumber(String ruleCode) {
        return generateNumber(ruleCode, LocalDate.now());
    }

    /**
     * 根据规则编码和业务日期生成下一个编号
     *
     * @param ruleCode 规则编码
     * @param bizDate  业务日期
     * @return 生成的编号字符串
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateNumber(String ruleCode, LocalDate bizDate) {
        // 1. 查询编号规则
        SysNumberingRule rule = ruleMapper.selectOne(
                new LambdaQueryWrapper<SysNumberingRule>()
                        .eq(SysNumberingRule::getRuleCode, ruleCode)
        );
        if (rule == null || rule.getStatus() == null || rule.getStatus() != 1) {
            throw new BusinessException("编号规则未找到或已禁用: " + ruleCode);
        }

        // 2. 获取或初始化日计数器
        SysNumberingSequence seq = sequenceMapper.selectOne(
                new LambdaQueryWrapper<SysNumberingSequence>()
                        .eq(SysNumberingSequence::getRuleCode, ruleCode)
                        .eq(SysNumberingSequence::getBizDate, bizDate)
        );

        if (seq == null) {
            // 首次使用：创建计数器，从1开始
            seq = new SysNumberingSequence();
            seq.setRuleCode(ruleCode);
            seq.setBizDate(bizDate);
            seq.setCurrentSeq(0);
            sequenceMapper.insert(seq);
        }

        // 3. 行锁 + 递增（防止并发重复）
        sequenceMapper.selectForUpdate(ruleCode, bizDate.toString());
        sequenceMapper.incrementSeq(ruleCode, bizDate.toString());

        // 4. 重新读取最新的序列值
        SysNumberingSequence updated = sequenceMapper.selectOne(
                new LambdaQueryWrapper<SysNumberingSequence>()
                        .eq(SysNumberingSequence::getRuleCode, ruleCode)
                        .eq(SysNumberingSequence::getBizDate, bizDate)
        );
        int seqValue = updated.getCurrentSeq();

        // 5. 格式化编号
        return formatNumber(rule, bizDate, seqValue);
    }

    /**
     * 按照规则格式化编号
     */
    private String formatNumber(SysNumberingRule rule, LocalDate bizDate, int seqValue) {
        StringBuilder sb = new StringBuilder();

        // 前缀
        if (rule.getPrefix() != null && !rule.getPrefix().isEmpty()) {
            sb.append(rule.getPrefix());
        }

        // 分隔符
        String sep = rule.getSeparator() != null ? rule.getSeparator() : "-";

        // 日期
        if (rule.getDatePattern() != null && !rule.getDatePattern().isEmpty()) {
            sb.append(sep);
            sb.append(bizDate.format(DateTimeFormatter.ofPattern(rule.getDatePattern())));
        }

        // 流水号
        sb.append(sep);
        int seqLen = rule.getSeqLength() != null ? rule.getSeqLength() : 4;
        sb.append(String.format("%0" + seqLen + "d", seqValue));

        return sb.toString();
    }
}
