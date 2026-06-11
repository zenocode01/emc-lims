package com.emclims.common.numbering;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysNumberingRule;
import com.emclims.module.sys.entity.SysNumberingSequence;
import com.emclims.module.sys.mapper.SysNumberingRuleMapper;
import com.emclims.module.sys.mapper.SysNumberingSequenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 编号规则引擎单元测试
 */
@ExtendWith(MockitoExtension.class)
class NumberingRuleEngineTest {

    @Mock
    private SysNumberingRuleMapper ruleMapper;

    @Mock
    private SysNumberingSequenceMapper sequenceMapper;

    private NumberingRuleEngine engine;

    private SysNumberingRule sampleRule;

    @BeforeEach
    void setUp() {
        engine = new NumberingRuleEngine(ruleMapper, sequenceMapper);

        sampleRule = new SysNumberingRule();
        sampleRule.setRuleCode("SAMPLE_DEFAULT");
        sampleRule.setRuleName("样品编号规则");
        sampleRule.setModuleType("sample");
        sampleRule.setPrefix("EMC");
        sampleRule.setDatePattern("yyyyMMdd");
        sampleRule.setSeqLength(4);
        sampleRule.setSeparator("-");
        sampleRule.setStatus(1);
    }

    @Test
    void testGenerateNumber_NewDay_CreatesSequence() {
        LocalDate today = LocalDate.of(2026, 6, 11);

        // 规则查询：返回样品编号规则
        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);

        // selectForUpdate 返回 null（该日期无记录），需创建
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(null);

        // 递增成功
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        // 执行
        String number = engine.generateNumber("SAMPLE_DEFAULT", today);

        // 验证格式：EMC-20260611-0001
        assertNotNull(number);
        assertTrue(number.startsWith("EMC-20260611-"));
        assertEquals("EMC-20260611-0001", number);

        // 验证序列插入
        verify(sequenceMapper).insert(any(SysNumberingSequence.class));

        // 验证递增
        verify(sequenceMapper).incrementSeq("SAMPLE_DEFAULT", "2026-06-11");
    }

    @Test
    void testGenerateNumber_ExistingDay_IncrementsSeq() {
        LocalDate today = LocalDate.of(2026, 6, 11);

        // 规则查询
        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);

        // selectForUpdate 返回当前值 3
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(3);

        // 递增
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        // 执行
        String number = engine.generateNumber("SAMPLE_DEFAULT", today);

        // 验证：3 + 1 = 4，序列号应为 0004
        assertEquals("EMC-20260611-0004", number);

        // 验证未插入新序列
        verify(sequenceMapper, never()).insert(any(SysNumberingSequence.class));
    }

    @Test
    void testGenerateNumber_RuleNotFound_ThrowsException() {
        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () ->
                engine.generateNumber("NONEXISTENT_RULE")
        );
    }

    @Test
    void testGenerateNumber_RuleDisabled_ThrowsException() {
        sampleRule.setStatus(0);
        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);

        assertThrows(BusinessException.class, () ->
                engine.generateNumber("SAMPLE_DEFAULT")
        );
    }

    @Test
    void testGenerateNumber_DefaultDate_UsesToday() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(null);
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        String number = engine.generateNumber("SAMPLE_DEFAULT");

        assertTrue(number.startsWith("EMC-" + todayStr));
    }

    @Test
    void testGenerateNumber_NoPrefix_NoDatePattern() {
        // 测试没有前缀和日期格式的情况
        sampleRule.setPrefix(null);
        sampleRule.setDatePattern(null);

        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(null);
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        String number = engine.generateNumber("SAMPLE_DEFAULT", LocalDate.of(2026, 6, 11));

        // 没有前缀和日期，只有流水号：-0001
        assertEquals("-0001", number);
    }

    @Test
    void testGenerateNumber_ConcurrentNewDay_DuplicateKeyTriggersRetry() {
        LocalDate today = LocalDate.of(2026, 6, 11);

        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);

        // selectForUpdate 第一次返回 null（无记录）
        // insert 时抛出 DuplicateKeyException（并发插入）
        // selectForUpdate 第二次返回 3（对手已插入并递增）
        when(sequenceMapper.selectForUpdate(anyString(), anyString()))
                .thenReturn(null)
                .thenReturn(3);
        when(sequenceMapper.insert(any(SysNumberingSequence.class)))
                .thenThrow(new DuplicateKeyException("唯一约束冲突"));
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        String number = engine.generateNumber("SAMPLE_DEFAULT", today);

        // 验证：捕获冲突后重试，基于对手的 currentSeq(3) + 1 = 4
        assertEquals("EMC-20260611-0004", number);

        // 验证 retry 时再次调用了 selectForUpdate + incrementSeq
        verify(sequenceMapper, times(2)).selectForUpdate(anyString(), anyString());
        verify(sequenceMapper, times(2)).incrementSeq(anyString(), anyString());
        verify(sequenceMapper, times(1)).insert(any(SysNumberingSequence.class));
    }

    /**
     * 创建测试用的序列记录
     */
    private SysNumberingSequence createSequence(String ruleCode, LocalDate bizDate, int currentSeq) {
        SysNumberingSequence seq = new SysNumberingSequence();
        seq.setRuleCode(ruleCode);
        seq.setBizDate(bizDate);
        seq.setCurrentSeq(currentSeq);
        return seq;
    }
}
