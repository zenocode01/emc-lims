package com.emclims.module.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.personnel.dto.PersonnelAuthorizationDTO;
import com.emclims.module.personnel.dto.PersonnelAuthorizationQueryDTO;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.entity.PersonnelAuthorization;
import com.emclims.module.personnel.mapper.PersonnelAuthorizationMapper;
import com.emclims.module.personnel.mapper.PersonnelMapper;
import com.emclims.module.personnel.service.PersonnelAuthorizationService;
import com.emclims.module.personnel.vo.PersonnelAuthorizationVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权上岗记录 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelAuthorizationServiceImpl extends ServiceImpl<PersonnelAuthorizationMapper, PersonnelAuthorization>
        implements PersonnelAuthorizationService {

    private final PersonnelMapper personnelMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 状态码映射
     */
    private static final Map<String, String> STATUS_MAP = Map.of(
            "0", "已过期",
            "1", "有效",
            "2", "即将过期"
    );

    /**
     * 分页查询授权上岗记录
     */
    @Override
    public Page<PersonnelAuthorizationVO> pageAuthorizations(PersonnelAuthorizationQueryDTO queryDTO) {
        LambdaQueryWrapper<PersonnelAuthorization> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getPersonnelId() != null,
                        PersonnelAuthorization::getPersonnelId, queryDTO.getPersonnelId())
                .like(StrUtil.isNotBlank(queryDTO.getAuthorizationItem()),
                        PersonnelAuthorization::getAuthorizationItem, queryDTO.getAuthorizationItem())
                .ge(queryDTO.getAuthorizationDateStart() != null,
                        PersonnelAuthorization::getAuthorizationDate, queryDTO.getAuthorizationDateStart())
                .le(queryDTO.getAuthorizationDateEnd() != null,
                        PersonnelAuthorization::getAuthorizationDate, queryDTO.getAuthorizationDateEnd())
                .ge(queryDTO.getExpireDateStart() != null,
                        PersonnelAuthorization::getExpireDate, queryDTO.getExpireDateStart())
                .le(queryDTO.getExpireDateEnd() != null,
                        PersonnelAuthorization::getExpireDate, queryDTO.getExpireDateEnd())
                .orderByDesc(PersonnelAuthorization::getCreateTime);

        Page<PersonnelAuthorization> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<PersonnelAuthorization> resultPage = this.page(page, wrapper);

        List<PersonnelAuthorizationVO> voList = convertToVOList(resultPage.getRecords());
        Page<PersonnelAuthorizationVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取授权上岗记录详情
     */
    @Override
    public PersonnelAuthorizationVO getAuthorizationDetail(Long id) {
        PersonnelAuthorization authorization = this.getById(id);
        if (authorization == null) {
            throw new BusinessException("授权上岗记录不存在");
        }
        List<PersonnelAuthorization> list = new ArrayList<>();
        list.add(authorization);
        List<PersonnelAuthorizationVO> vos = convertToVOList(list);
        return vos.isEmpty() ? null : vos.get(0);
    }

    /**
     * 新增授权上岗记录
     */
    @Override
    public void addAuthorization(PersonnelAuthorizationDTO dto) {
        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        PersonnelAuthorization authorization = new PersonnelAuthorization();
        BeanUtils.copyProperties(dto, authorization);
        this.save(authorization);
        log.info("新增授权上岗记录，记录ID: {}, 人员ID: {}, 授权项目: {}",
                authorization.getId(), authorization.getPersonnelId(), authorization.getAuthorizationItem());
    }

    /**
     * 更新授权上岗记录
     */
    @Override
    public void updateAuthorization(PersonnelAuthorizationDTO dto) {
        PersonnelAuthorization authorization = this.getById(dto.getId());
        if (authorization == null) {
            throw new BusinessException("授权上岗记录不存在");
        }

        // 验证人员是否存在
        Personnel personnel = personnelMapper.selectById(dto.getPersonnelId());
        if (personnel == null) {
            throw new BusinessException("人员档案不存在");
        }

        BeanUtils.copyProperties(dto, authorization, "id", "createTime", "updateTime", "createBy", "updateBy", "deleted");
        this.updateById(authorization);
        log.info("更新授权上岗记录，记录ID: {}, 人员ID: {}, 授权项目: {}",
                authorization.getId(), authorization.getPersonnelId(), authorization.getAuthorizationItem());
    }

    /**
     * 批量删除授权上岗记录
     */
    @Override
    public void deleteAuthorizations(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除列表不能为空");
        }
        this.removeByIds(ids);
        log.info("批量删除授权上岗记录，删除数量: {}", ids.size());
    }

    /**
     * 根据人员ID查询授权记录列表
     */
    @Override
    public List<PersonnelAuthorizationVO> listByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<PersonnelAuthorization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonnelAuthorization::getPersonnelId, personnelId)
                .orderByDesc(PersonnelAuthorization::getAuthorizationDate);
        List<PersonnelAuthorization> list = this.list(wrapper);
        return convertToVOList(list);
    }

    /**
     * 将实体列表转换为 VO 列表，填充关联数据
     */
    private List<PersonnelAuthorizationVO> convertToVOList(List<PersonnelAuthorization> list) {
        // 批量查询人员信息
        List<Long> personnelIds = list.stream()
                .map(PersonnelAuthorization::getPersonnelId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Personnel> personnelMap = personnelIds.isEmpty() ? Map.of()
                : personnelMapper.selectBatchIds(personnelIds).stream()
                        .collect(Collectors.toMap(Personnel::getId, Function.identity()));

        // 批量查询授权人信息
        List<Long> authorizerIds = list.stream()
                .map(PersonnelAuthorization::getAuthorizerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> authorizerMap = authorizerIds.isEmpty() ? Map.of()
                : sysUserMapper.selectBatchIds(authorizerIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        return list.stream().map(authorization -> {
            PersonnelAuthorizationVO vo = new PersonnelAuthorizationVO();
            BeanUtils.copyProperties(authorization, vo);

            // 填充人员姓名
            if (authorization.getPersonnelId() != null) {
                Personnel personnel = personnelMap.get(authorization.getPersonnelId());
                if (personnel != null) {
                    vo.setPersonnelName(personnel.getName());
                }
            }

            // 填充授权人姓名
            if (authorization.getAuthorizerId() != null) {
                SysUser sysUser = authorizerMap.get(authorization.getAuthorizerId());
                if (sysUser != null) {
                    vo.setAuthorizerName(sysUser.getNickname());
                }
            }

            // 计算状态
            vo.setStatus(calculateStatus(authorization.getExpireDate()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 计算授权状态
     * 0-已过期，1-有效，2-即将过期（30天内）
     */
    private String calculateStatus(LocalDate expireDate) {
        if (expireDate == null) {
            return "0";
        }
        LocalDate today = LocalDate.now();
        if (expireDate.isBefore(today)) {
            return "0";
        }
        long daysUntilExpire = ChronoUnit.DAYS.between(today, expireDate);
        if (daysUntilExpire <= 30) {
            return "2";
        }
        return "1";
    }
}
