package com.emclims.module.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.vo.SysDeptExportVO;
import com.emclims.module.sys.vo.SysDeptVO;

import java.util.List;

/**
 * 部门 Service
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 获取部门树
     */
    List<SysDeptVO> getDeptTree();

    /**
     * 根据ID获取部门详情
     */
    SysDeptVO getDeptDetail(Long id);

    /**
     * 新增部门
     */
    void createDept(SysDept dept);

    /**
     * 更新部门
     */
    void updateDept(SysDept dept);

    /**
     * 删除部门
     */
    void deleteDept(Long id);

    /**
     * 导出部门列表
     */
    List<SysDeptExportVO> exportDepts();
}
