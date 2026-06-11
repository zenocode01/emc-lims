package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.service.SysDeptService;
import com.emclims.module.sys.vo.SysDeptVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门 Service 实现
 */
@Slf4j
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDeptVO> getDeptTree() {
        log.debug("获取部门树结构");
        List<SysDept> allDepts = this.list();
        List<SysDeptVO> voList = allDepts.stream().map(dept -> {
            SysDeptVO vo = new SysDeptVO();
            BeanUtils.copyProperties(dept, vo);
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        return buildTree(voList, 0L);
    }

    private List<SysDeptVO> buildTree(List<SysDeptVO> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId() != null && dept.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysDeptVO::getSort))
                .map(dept -> {
                    dept.setChildren(buildTree(allDepts, dept.getId()));
                    return dept;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SysDeptVO getDeptDetail(Long id) {
        log.debug("获取部门详情，部门ID: {}", id);
        SysDept dept = this.getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        SysDeptVO vo = new SysDeptVO();
        BeanUtils.copyProperties(dept, vo);

        if (dept.getParentId() != null && dept.getParentId() > 0) {
            SysDept parent = this.getById(dept.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getDeptName());
            }
        }
        return vo;
    }

    @Override
    public void createDept(SysDept dept) {
        log.info("创建部门，部门名称: {}, 部门编码: {}", dept.getDeptName(), dept.getDeptCode());
        if (dept.getParentId() == null || dept.getParentId() == 0) {
            dept.setParentId(0L);
        }
        this.save(dept);
    }

    @Override
    public void updateDept(SysDept dept) {
        log.info("更新部门信息，部门ID: {}, 部门名称: {}", dept.getId(), dept.getDeptName());
        this.updateById(dept);
    }

    @Override
    public void deleteDept(Long id) {
        log.info("删除部门，部门ID: {}", id);
        // 检查是否有子部门
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该部门下还有子部门，不能删除");
        }
        this.removeById(id);
    }
}
