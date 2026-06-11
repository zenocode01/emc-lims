package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.mapper.SysDeptMapper;
import com.emclims.module.sys.vo.SysDeptVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysDeptServiceImpl 部门服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysDeptServiceImplTest {

    @Mock
    private SysDeptMapper deptMapper;

    @InjectMocks
    private SysDeptServiceImpl service;


    @Test
    void testGetDeptTree() {
        SysDept dept1 = createDept(1L, "总公司", 0L);
        SysDept dept2 = createDept(2L, "技术部", 1L);
        when(deptMapper.selectList(any())).thenReturn(List.of(dept1, dept2));

        List<SysDeptVO> tree = service.getDeptTree();
        assertEquals(1, tree.size());
        assertEquals("总公司", tree.get(0).getDeptName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("技术部", tree.get(0).getChildren().get(0).getDeptName());
    }

    @Test
    void testGetDeptDetail() {
        SysDept dept = createDept(1L, "技术部", 0L);
        when(deptMapper.selectById(1L)).thenReturn(dept);

        SysDeptVO vo = service.getDeptDetail(1L);
        assertNotNull(vo);
        assertEquals("技术部", vo.getDeptName());
    }

    @Test
    void testGetDeptDetailWithParent() {
        SysDept dept = createDept(2L, "测试组", 1L);
        SysDept parent = createDept(1L, "技术部", 0L);
        when(deptMapper.selectById(2L)).thenReturn(dept);
        when(deptMapper.selectById(1L)).thenReturn(parent);

        SysDeptVO vo = service.getDeptDetail(2L);
        assertEquals("测试组", vo.getDeptName());
        assertEquals("技术部", vo.getParentName());
    }

    @Test
    void testGetDeptDetailNotFound() {
        when(deptMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.getDeptDetail(999L));
    }

    @Test
    void testCreateDept() {
        SysDept dept = new SysDept();
        dept.setDeptName("新部门");
        dept.setParentId(1L);

        service.createDept(dept);
        verify(deptMapper).insert(dept);
    }

    @Test
    void testCreateDeptNullParent() {
        SysDept dept = new SysDept();
        dept.setDeptName("顶级部门");

        service.createDept(dept);
        assertEquals(0L, dept.getParentId());
    }

    @Test
    void testUpdateDept() {
        SysDept dept = createDept(1L, "更新名称", 0L);
        service.updateDept(dept);
        verify(deptMapper).updateById(dept);
    }

    @Test
    void testDeleteDeptSuccess() {
        SysDeptServiceImpl spy = spy(service);
        when(deptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        doReturn(true).when(spy).removeById(1L);
        spy.deleteDept(1L);
        verify(spy).removeById(1L);
    }

    @Test
    void testDeleteDeptHasChildren() {
        when(deptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        assertThrows(BusinessException.class, () -> service.deleteDept(1L), "该部门下还有子部门，不能删除");
        verify(deptMapper, never()).deleteById(any());
    }

    private SysDept createDept(Long id, String name, Long parentId) {
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setDeptName(name);
        dept.setParentId(parentId);
        return dept;
    }
}
