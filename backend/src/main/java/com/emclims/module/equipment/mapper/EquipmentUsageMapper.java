package com.emclims.module.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.equipment.entity.EquipmentUsage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备使用记录 Mapper
 */
@Mapper
public interface EquipmentUsageMapper extends BaseMapper<EquipmentUsage> {
}
