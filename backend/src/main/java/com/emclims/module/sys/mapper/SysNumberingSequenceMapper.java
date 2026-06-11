package com.emclims.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.sys.entity.SysNumberingSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 编号序列计数器 Mapper
 */
@Mapper
public interface SysNumberingSequenceMapper extends BaseMapper<SysNumberingSequence> {

    /**
     * 锁定并递增序列号（通过数据库行锁保证并发安全）
     */
    @Select("SELECT current_seq FROM sys_numbering_sequence WHERE rule_code = #{ruleCode} AND biz_date = #{bizDate} FOR UPDATE")
    Integer selectForUpdate(@Param("ruleCode") String ruleCode, @Param("bizDate") String bizDate);

    /**
     * 递增序列号
     */
    @Update("UPDATE sys_numbering_sequence SET current_seq = current_seq + 1, update_time = NOW() WHERE rule_code = #{ruleCode} AND biz_date = #{bizDate}")
    int incrementSeq(@Param("ruleCode") String ruleCode, @Param("bizDate") String bizDate);
}
