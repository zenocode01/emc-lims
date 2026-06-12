package com.emclims.module.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.personnel.dto.PersonnelTrainingDTO;
import com.emclims.module.personnel.dto.PersonnelTrainingQueryDTO;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.entity.PersonnelTraining;
import com.emclims.module.personnel.mapper.PersonnelMapper;
import com.emclims.module.personnel.mapper.PersonnelTrainingMapper;
import com.emclims.module.personnel.service.PersonnelTrainingService;
import com.emclims.module.personnel.vo.PersonnelTrainingVO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 培训记录 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelTrainingServiceImpl extends ServiceImpl<PersonnelTrainingMapper, PersonnelTraining>
        implements PersonnelTrainingService {

    private final PersonnelMapper personnelMapper;

    /**
     * 状态码映射
     */
    private static final Map<String, String> RESULT_MAP = Map.of(
            "0", "不合格",
            "1", "合格"
    );

    /**
     * 分页查询培训记录
     */
    @Override
    public Page<PersonnelTrainingVO> pageTrainings(PersonnelTrainingQueryDTO queryDTO) {
        LambdaQueryWrapper<PersonnelTraining> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getPersonnelId() != null,
                        PersonnelTraining::getPersonnelId, queryDTO.getPersonnelId())
                .like(StrUtil.isNotBlank(queryDTO.getCourse()),
                        PersonnelTraining::getCourse, queryDTO.getCourse())
                .like(StrUtil.isNotBlank(queryDTO.getTrainer()),
                        PersonnelTraining::getTrainer, queryDTO.getTrainer())
                .ge(queryDTO.getTrainDateStart() != null,
                        PersonnelTraining::getTrainDate, queryDTO.getTrainDateStart())
                .le(queryDTO.getTrainDateEnd() != null,
                        PersonnelTraining::getTrainDate, queryDTO.getTrainDateEnd())
                .eq(StrUtil.isNotBlank(queryDTO.getResult()),
                        PersonnelTraining::getResult, queryDTO.getResult())
                .orderByDesc(PersonnelTraining::getTrainDate);

        Page<PersonnelTraining> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<PersonnelTraining> resultPage = this.page(page, wrapper);

        List<PersonnelTrainingVO> voList = convertToVOList(resultPage.getRecords());
        Page<PersonnelTrainingVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取培训记录详情
     */
    @Override
    public PersonnelTrainingVO getTrainingDetail(Long id) {
        PersonnelTraining training = this.getById(id);
        if (training == null) {
            throw new BusinessException("培训记录不存在");
        }
        List<PersonnelTraining> list = new ArrayList<>();
        list.add(training);
        List<PersonnelTrainingVO> vos = convertToVOList(list);
        return vos.isEmpty() ? null : vos.get(0);
    }

    /**
     * 新增培训记录
     */
    @Override
    public void addTraining(PersonnelTrainingDTO dto) {
        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        PersonnelTraining training = new PersonnelTraining();
        BeanUtils.copyProperties(dto, training);
        this.save(training);
        log.info("新增培训记录，记录ID: {}, 人员ID: {}, 课程: {}",
                training.getId(), training.getPersonnelId(), training.getCourse());
    }

    /**
     * 更新培训记录
     */
    @Override
    public void updateTraining(PersonnelTrainingDTO dto) {
        PersonnelTraining training = this.getById(dto.getId());
        if (training == null) {
            throw new BusinessException("培训记录不存在");
        }

        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        BeanUtils.copyProperties(dto, training, "id", "createTime", "updateTime", "createBy", "updateBy", "deleted");
        this.updateById(training);
        log.info("更新培训记录，记录ID: {}, 人员ID: {}, 课程: {}",
                training.getId(), training.getPersonnelId(), training.getCourse());
    }

    /**
     * 批量删除培训记录
     */
    @Override
    public void deleteTrainings(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除列表不能为空");
        }
        this.removeByIds(ids);
        log.info("批量删除培训记录，删除数量: {}", ids.size());
    }

    /**
     * 根据人员ID查询培训记录列表
     */
    @Override
    public List<PersonnelTrainingVO> listByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<PersonnelTraining> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonnelTraining::getPersonnelId, personnelId)
                .orderByDesc(PersonnelTraining::getTrainDate);
        List<PersonnelTraining> list = this.list(wrapper);
        return convertToVOList(list);
    }

    /**
     * 将实体列表转换为 VO 列表，填充关联数据
     */
    private List<PersonnelTrainingVO> convertToVOList(List<PersonnelTraining> list) {
        // 批量查询人员信息
        List<Long> personnelIds = list.stream()
                .map(PersonnelTraining::getPersonnelId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Personnel> personnelMap = personnelIds.isEmpty() ? Map.of()
                : personnelMapper.selectBatchIds(personnelIds).stream()
                        .collect(Collectors.toMap(Personnel::getId, Function.identity()));

        return list.stream().map(training -> {
            PersonnelTrainingVO vo = new PersonnelTrainingVO();
            BeanUtils.copyProperties(training, vo);

            // 填充人员姓名
            if (training.getPersonnelId() != null) {
                Personnel personnel = personnelMap.get(training.getPersonnelId());
                if (personnel != null) {
                    vo.setPersonnelName(personnel.getName());
                }
            }

            // 填充结果名称
            vo.setResultName(RESULT_MAP.getOrDefault(training.getResult(), training.getResult()));
            return vo;
        }).collect(Collectors.toList());
    }
}
