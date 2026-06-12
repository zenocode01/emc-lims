package com.emclims.module.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.personnel.dto.CompetencyMatrixDTO;
import com.emclims.module.personnel.dto.CompetencyMatrixQueryDTO;
import com.emclims.module.personnel.entity.CompetencyMatrix;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.mapper.CompetencyMatrixMapper;
import com.emclims.module.personnel.mapper.PersonnelMapper;
import com.emclims.module.personnel.service.CompetencyMatrixService;
import com.emclims.module.personnel.vo.CompetencyMatrixVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 能力矩阵 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompetencyMatrixServiceImpl extends ServiceImpl<CompetencyMatrixMapper, CompetencyMatrix>
        implements CompetencyMatrixService {

    private final PersonnelMapper personnelMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询能力矩阵
     */
    @Override
    public Page<CompetencyMatrixVO> pageCompetencyMatrix(CompetencyMatrixQueryDTO queryDTO) {
        LambdaQueryWrapper<CompetencyMatrix> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getPersonnelId() != null,
                        CompetencyMatrix::getPersonnelId, queryDTO.getPersonnelId())
                .eq(StrUtil.isNotBlank(queryDTO.getTestItemType()),
                        CompetencyMatrix::getTestItemType, queryDTO.getTestItemType())
                .ge(queryDTO.getAssessmentDateStart() != null,
                        CompetencyMatrix::getAssessmentDate, queryDTO.getAssessmentDateStart())
                .le(queryDTO.getAssessmentDateEnd() != null,
                        CompetencyMatrix::getAssessmentDate, queryDTO.getAssessmentDateEnd())
                .ge(queryDTO.getMinScore() != null,
                        CompetencyMatrix::getScore, queryDTO.getMinScore())
                .le(queryDTO.getMaxScore() != null,
                        CompetencyMatrix::getScore, queryDTO.getMaxScore())
                .orderByDesc(CompetencyMatrix::getAssessmentDate);

        Page<CompetencyMatrix> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<CompetencyMatrix> resultPage = this.page(page, wrapper);

        List<CompetencyMatrixVO> voList = convertToVOList(resultPage.getRecords());
        Page<CompetencyMatrixVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取能力矩阵详情
     */
    @Override
    public CompetencyMatrixVO getCompetencyDetail(Long id) {
        CompetencyMatrix matrix = this.getById(id);
        if (matrix == null) {
            throw new BusinessException("能力矩阵记录不存在");
        }
        List<CompetencyMatrix> list = new ArrayList<>();
        list.add(matrix);
        List<CompetencyMatrixVO> vos = convertToVOList(list);
        return vos.isEmpty() ? null : vos.get(0);
    }

    /**
     * 新增能力矩阵记录
     */
    @Override
    public void addCompetencyMatrix(CompetencyMatrixDTO dto) {
        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        CompetencyMatrix matrix = new CompetencyMatrix();
        BeanUtils.copyProperties(dto, matrix);
        this.save(matrix);
        log.info("新增能力矩阵记录，记录ID: {}, 人员ID: {}, 测试项目: {}",
                matrix.getId(), matrix.getPersonnelId(), matrix.getTestItemType());
    }

    /**
     * 更新能力矩阵记录
     */
    @Override
    public void updateCompetencyMatrix(CompetencyMatrixDTO dto) {
        CompetencyMatrix matrix = this.getById(dto.getId());
        if (matrix == null) {
            throw new BusinessException("能力矩阵记录不存在");
        }

        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        BeanUtils.copyProperties(dto, matrix, "id", "createTime", "updateTime", "createBy", "updateBy", "deleted");
        this.updateById(matrix);
        log.info("更新能力矩阵记录，记录ID: {}, 人员ID: {}, 测试项目: {}",
                matrix.getId(), matrix.getPersonnelId(), matrix.getTestItemType());
    }

    /**
     * 批量删除能力矩阵记录
     */
    @Override
    public void deleteCompetencyMatrices(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除列表不能为空");
        }
        this.removeByIds(ids);
        log.info("批量删除能力矩阵记录，删除数量: {}", ids.size());
    }

    /**
     * 根据人员ID查询能力矩阵列表
     */
    @Override
    public List<CompetencyMatrixVO> listByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<CompetencyMatrix> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetencyMatrix::getPersonnelId, personnelId)
                .orderByDesc(CompetencyMatrix::getAssessmentDate);
        List<CompetencyMatrix> list = this.list(wrapper);
        return convertToVOList(list);
    }

    /**
     * 根据人员和测试项目查询能力矩阵
     */
    @Override
    public CompetencyMatrixVO getByPersonnelAndItemType(Long personnelId, String testItemType) {
        LambdaQueryWrapper<CompetencyMatrix> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetencyMatrix::getPersonnelId, personnelId)
                .eq(CompetencyMatrix::getTestItemType, testItemType)
                .last("LIMIT 1");
        CompetencyMatrix matrix = this.getOne(wrapper);
        if (matrix == null) {
            return null;
        }
        List<CompetencyMatrix> list = new ArrayList<>();
        list.add(matrix);
        List<CompetencyMatrixVO> vos = convertToVOList(list);
        return vos.isEmpty() ? null : vos.get(0);
    }

    /**
     * 将实体列表转换为 VO 列表，填充关联数据
     */
    private List<CompetencyMatrixVO> convertToVOList(List<CompetencyMatrix> list) {
        // 批量查询人员信息
        List<Long> personnelIds = list.stream()
                .map(CompetencyMatrix::getPersonnelId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Personnel> personnelMap = personnelIds.isEmpty() ? Map.of()
                : personnelMapper.selectBatchIds(personnelIds).stream()
                        .collect(Collectors.toMap(Personnel::getId, Function.identity()));

        // 批量查询考核人信息
        List<Long> assessorIds = list.stream()
                .map(CompetencyMatrix::getAssessorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> assessorMap = assessorIds.isEmpty() ? Map.of()
                : sysUserMapper.selectBatchIds(assessorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        return list.stream().map(matrix -> {
            CompetencyMatrixVO vo = new CompetencyMatrixVO();
            BeanUtils.copyProperties(matrix, vo);

            // 填充人员姓名
            if (matrix.getPersonnelId() != null) {
                Personnel personnel = personnelMap.get(matrix.getPersonnelId());
                if (personnel != null) {
                    vo.setPersonnelName(personnel.getName());
                }
            }

            // 填充考核人姓名
            if (matrix.getAssessorId() != null) {
                SysUser sysUser = assessorMap.get(matrix.getAssessorId());
                if (sysUser != null) {
                    vo.setAssessorName(sysUser.getNickname());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
