package com.emclims.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.emclims.common.security.SecurityUtils;
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
        
        // 填充创建人和修改人
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 填充修改人
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
        }
    }
}
