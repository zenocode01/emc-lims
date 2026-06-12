package com.emclims.module.audit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.common.response.PageResult;
import com.emclims.module.audit.dto.OperationLogQueryDTO;
import com.emclims.module.audit.entity.OperationLog;
import com.emclims.module.audit.vo.OperationLogVO;

/**
 * 操作日志 Service
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 分页查询操作日志
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<OperationLogVO> pageLogs(OperationLogQueryDTO queryDTO);

    /**
     * 获取操作日志详情
     * @param id 日志 ID
     * @return 日志详情
     */
    OperationLogVO getLogDetail(Long id);

    /**
     * 批量删除操作日志
     * @param ids 日志 ID 列表
     */
    void deleteLogs(Long[] ids);
}
