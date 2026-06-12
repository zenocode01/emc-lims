package com.emclims.module.sample.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.sample.dto.SampleDTO;
import com.emclims.module.sample.dto.SampleQueryDTO;
import com.emclims.module.sample.dto.SampleStatusDTO;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.vo.SampleExportVO;
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;

import java.util.List;

/**
 * 样品 Service
 */
public interface SampleService extends IService<Sample> {

    /**
     * 分页查询样品列表
     */
    Page<SampleVO> pageSamples(SampleQueryDTO queryDTO);

    /**
     * 根据ID获取样品详情
     */
    SampleVO getSampleDetail(Long id);

    /**
     * 收样登记（新增样品）
     */
    void receiveSample(SampleDTO dto);

    /**
     * 更新样品信息
     */
    void updateSample(SampleDTO dto);

    /**
     * 批量删除样品
     */
    void deleteSamples(List<Long> ids);

    /**
     * 变更样品状态（含流转日志记录）
     */
    void changeStatus(SampleStatusDTO dto);

    /**
     * 获取样品流转日志
     */
    List<SampleLogVO> getSampleLogs(Long sampleId);

    /**
     * 导出样品列表
     */
    List<SampleExportVO> exportSamples(SampleQueryDTO queryDTO);
}
