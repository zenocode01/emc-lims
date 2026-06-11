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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        // 序列查询（第一次）：不存在，返回 null
        when(sequenceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null)
                .thenReturn(createSequence("SAMPLE_DEFAULT", today, 1));

        // 插入成功
        when(sequenceMapper.insert(any(SysNumberingSequence.class))).thenReturn(1);

        // 递增成功
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        // 锁定序列
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(0);

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

        // 序列查询（已有记录当前值为 3）
        SysNumberingSequence existingSeq = createSequence("SAMPLE_DEFAULT", today, 3);
        when(sequenceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingSeq)
                .thenReturn(createSequence("SAMPLE_DEFAULT", today, 4));

        // 递增
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);

        // 锁定
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(3);

        // 执行
        String number = engine.generateNumber("SAMPLE_DEFAULT", today);

        // 验证：序列号应为 0004（在第4次调用）
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
        when(sequenceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null)
                .thenReturn(createSequence("SAMPLE_DEFAULT", today, 1));
        when(sequenceMapper.insert(any(SysNumberingSequence.class))).thenReturn(1);
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(0);

        String number = engine.generateNumber("SAMPLE_DEFAULT");

        assertTrue(number.startsWith("EMC-" + todayStr));
    }

    @Test
    void testGenerateNumber_NoPrefix_NoDatePattern() {
        // 测试没有前缀和日期格式的情况
        sampleRule.setPrefix(null);
        sampleRule.setDatePattern(null);

        when(ruleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(sampleRule);
        when(sequenceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null)
                .thenReturn(createSequence("SAMPLE_DEFAULT", LocalDate.of(2026, 6, 11), 1));
        when(sequenceMapper.insert(any(SysNumberingSequence.class))).thenReturn(1);
        when(sequenceMapper.incrementSeq(anyString(), anyString())).thenReturn(1);
        when(sequenceMapper.selectForUpdate(anyString(), anyString())).thenReturn(0);

        String number = engine.generateNumber("SAMPLE_DEFAULT", LocalDate.of(2026, 6, 11));

        // 没有前缀和日期，只有流水号：-0001
        assertEquals("-0001", number);
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
