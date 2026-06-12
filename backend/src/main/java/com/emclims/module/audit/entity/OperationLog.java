package com.emclims.module.audit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.emclims.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作审计日志实体
 * 对应数据库 operation_log 表
 * 记录所有业务模块的操作日志
 */
@TableName("operation_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLog extends BaseEntity {

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作模块：user-用户，role-角色，customer-客户，sample-样品等 */
    private String module;

    /** 操作类型：create-新增，update-修改，delete-删除，export-导出，review-审核等 */
    private String action;

    /** 操作描述 */
    private String description;

    /** 操作前数据（JSON 格式） */
    private String beforeData;

    /** 操作后数据（JSON 格式） */
    private String afterData;

    /** 请求 IP */
    private String requestIp;

    /** 请求 URL */
    private String requestUrl;

    /** 请求方法 */
    private String requestMethod;

    /** 请求参数（JSON 格式） */
    private String requestParams;

    /** 响应状态码 */
    private Integer responseCode;

    /** 响应耗时（毫秒） */
    private Long responseTime;

    /** 操作时间 */
    private LocalDateTime operationTime;

    /** 备注 */
    private String remark;
}
