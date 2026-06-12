package com.emclims.module.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.personnel.entity.PersonnelAuthorization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 授权上岗记录 Mapper
 */
@Mapper
public interface PersonnelAuthorizationMapper extends BaseMapper<PersonnelAuthorization> {
}
