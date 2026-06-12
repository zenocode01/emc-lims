package com.emclims.module.test.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.test.dto.TestItemDTO;
import com.emclims.module.test.dto.TestItemQueryDTO;
import com.emclims.module.test.entity.TestItem;
import com.emclims.module.test.mapper.TestItemMapper;
import com.emclims.module.test.service.TestItemService;
import com.emclims.module.test.vo.TestItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 测试项目 Service 实现
 */
@Slf4j
@Service
public class TestItemServiceImpl extends ServiceImpl<TestItemMapper, TestItem> implements TestItemService {

    @Override
    public Page<TestItemVO> pageTestItems(TestItemQueryDTO queryDTO) {
        log.debug("分页查询测试项目，关键字: {}, 类别: {}, 状态: {}", queryDTO.getKeyword(), queryDTO.getCategory(), queryDTO.getStatus());

        LambdaQueryWrapper<TestItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), TestItem::getCode, queryDTO.getKeyword())
               .like(StrUtil.isNotBlank(queryDTO.getKeyword()), TestItem::getName, queryDTO.getKeyword())
               .eq(StrUtil.isNotBlank(queryDTO.getCategory()), TestItem::getCategory, queryDTO.getCategory())
               .eq(queryDTO.getStatus() != null, TestItem::getStatus, queryDTO.getStatus())
               .orderByDesc(TestItem::getCreateTime);

        Page<TestItem> page = this.page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);

        Page<TestItemVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public TestItemVO getTestItemDetail(Long id) {
        log.debug("获取测试项目详情，ID: {}", id);
        TestItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException("测试项目不存在");
        }
        return convertToVO(item);
    }

    @Override
    public void createTestItem(TestItemDTO dto) {
        log.info("新增测试项目，编号: {}, 名称: {}", dto.getCode(), dto.getName());
        TestItem item = new TestItem();
        BeanUtils.copyProperties(dto, item);
        this.save(item);
    }

    @Override
    public void updateTestItem(TestItemDTO dto) {
        log.info("更新测试项目，ID: {}", dto.getId());
        TestItem item = this.getById(dto.getId());
        if (item == null) {
            throw new BusinessException("测试项目不存在");
        }
        BeanUtils.copyProperties(dto, item, "id", "createTime");
        this.updateById(item);
    }

    @Override
    public void deleteTestItem(Long id) {
        log.info("删除测试项目，ID: {}", id);
        TestItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException("测试项目不存在");
        }
        this.removeById(id);
    }

    @Override
    public void updateTestItemStatus(Long id, Integer status) {
        log.info("修改测试项目状态，ID: {}, 状态: {}", id, status);
        TestItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException("测试项目不存在");
        }
        item.setStatus(status);
        this.updateById(item);
    }

    /**
     * 转换为 VO
     */
    private TestItemVO convertToVO(TestItem item) {
        TestItemVO vo = new TestItemVO();
        BeanUtils.copyProperties(item, vo);
        vo.setCategoryName(convertCategoryName(item.getCategory()));
        vo.setStatusName(convertStatusName(item.getStatus()));
        return vo;
    }

    /**
     * 转换类别名称
     */
    private String convertCategoryName(String category) {
        if (category == null) {
            return "";
        }
        return "emission".equals(category) ? "发射" : "immunity".equals(category) ? "抗扰度" : category;
    }

    /**
     * 转换状态名称
     */
    private String convertStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        return status == 1 ? "启用" : "禁用";
    }
}
