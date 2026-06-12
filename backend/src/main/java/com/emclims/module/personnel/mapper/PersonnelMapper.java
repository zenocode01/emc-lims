package com.emclims.module.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.personnel.entity.Personnel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人员档案 Mapper
 */
@Mapper
public interface PersonnelMapper extends BaseMapper<Personnel> {
}
