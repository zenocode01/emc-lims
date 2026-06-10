package com.emclims.module.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.vo.SysDeptVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void testGetDeptTree() {
        SysDept dept1 = new SysDept();
        dept1.setId(1L);
        dept1.setDeptName("总公司");
        dept1.setParentId(0L);
        dept1.setSort(1);

        SysDept dept2 = new SysDept();
        dept2.setId(2L);
        dept2.setDeptName("技术部");
        dept2.setParentId(1L);
        dept2.setSort(1);

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(List.of(dept1, dept2)).when(spy).list();

        List<SysDeptVO> tree = spy.getDeptTree();
        assertEquals(1, tree.size());
        assertEquals("总公司", tree.get(0).getDeptName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("技术部", tree.get(0).getChildren().get(0).getDeptName());
    }

    @Test
    void testGetDeptDetail() {
        SysDept dept = new SysDept();
        dept.setId(1L);
        dept.setDeptName("技术部");
        dept.setParentId(0L);

        SysDept parent = new SysDept();
        parent.setId(0L);
        parent.setDeptName("总公司");

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(dept).when(spy).getById(1L);

        SysDeptVO vo = spy.getDeptDetail(1L);
        assertNotNull(vo);
        assertEquals("技术部", vo.getDeptName());
    }

    @Test
    void testGetDeptDetailWithParent() {
        SysDept dept = new SysDept();
        dept.setId(2L);
        dept.setDeptName("测试组");
        dept.setParentId(1L);

        SysDept parent = new SysDept();
        parent.setId(1L);
        parent.setDeptName("技术部");

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(dept).when(spy).getById(2L);
        doReturn(parent).when(spy).getById(1L);

        SysDeptVO vo = spy.getDeptDetail(2L);
        assertEquals("测试组", vo.getDeptName());
        assertEquals("技术部", vo.getParentName());
    }

    @Test
    void testGetDeptDetailNotFound() {
        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(null).when(spy).getById(999L);

        assertThrows(BusinessException.class, () -> spy.getDeptDetail(999L));
    }

    @Test
    void testCreateDept() {
        SysDept dept = new SysDept();
        dept.setDeptName("新部门");
        dept.setParentId(1L);

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(true).when(spy).save(any(SysDept.class));

        spy.createDept(dept);
        verify(spy).save(dept);
    }

    @Test
    void testCreateDeptNullParent() {
        SysDept dept = new SysDept();
        dept.setDeptName("顶级部门");

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(true).when(spy).save(any(SysDept.class));

        spy.createDept(dept);
        assertEquals(0L, dept.getParentId()); // 自动设置为顶级
    }

    @Test
    void testUpdateDept() {
        SysDept dept = new SysDept();
        dept.setId(1L);
        dept.setDeptName("更新名称");

        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(true).when(spy).updateById(any(SysDept.class));

        spy.updateDept(dept);
        verify(spy).updateById(dept);
    }

    @Test
    void testDeleteDeptSuccess() {
        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(0L).when(spy).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(spy).removeById(1L);

        spy.deleteDept(1L);
        verify(spy).removeById(1L);
    }

    @Test
    void testDeleteDeptHasChildren() {
        SysDeptServiceImpl spy = spy(new SysDeptServiceImpl());
        doReturn(1L).when(spy).count(any(LambdaQueryWrapper.class));

        assertThrows(BusinessException.class, () -> spy.deleteDept(1L), "该部门下还有子部门，不能删除");
        verify(spy, never()).removeById(any());
    }
}
