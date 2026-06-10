package com.emclims;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EMC LIMS 启动类
 * 电磁兼容实验室信息管理系统
 */
@SpringBootApplication
@MapperScan("com.emclims.module.*.mapper")
public class EmcLimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmcLimsApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  EMC LIMS 启动成功！");
        System.out.println("  API 文档: http://localhost:8080/api/doc.html");
        System.out.println("===========================================");
    }
}
