package com.emclims;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * EMC LIMS 应用上下文加载测试
 */
@SpringBootTest
@ActiveProfiles("test")
class EmcLimsApplicationTests {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文能够正常加载
    }
}
