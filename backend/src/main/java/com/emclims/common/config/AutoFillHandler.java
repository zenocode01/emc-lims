package com.emclims.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 字段自动填充处理器
 * 自动填充 createTime、updateTime、createBy、updateBy
 */
@Slf4j
@Component
public class AutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // TODO: 从 SecurityContext 获取当前用户
        // this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // TODO: 从 SecurityContext 获取当前用户
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }
}
