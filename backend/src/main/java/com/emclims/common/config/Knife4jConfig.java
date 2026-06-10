package com.emclims.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (SpringDoc OpenAPI 3) 配置
 *
 * 注意：本项目使用 knife4j-openapi3（基于 SpringDoc），
 * 不可混用 Springfox（Swagger 2）的 Docket API。
 */
@Configuration
public class Knife4jConfig {

    /**
     * 全局 API 文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EMC LIMS API 文档")
                        .description("电磁兼容实验室信息管理系统 - RESTful API 接口文档\n\n" +
                                "## 功能模块\n" +
                                "- 用户认证与权限管理\n" +
                                "- 部门管理\n" +
                                "- 角色与菜单管理\n" +
                                "- 客户管理\n" +
                                "- 样品管理\n" +
                                "- 测试管理\n" +
                                "- 报告管理\n\n" +
                                "## 数据权限\n" +
                                "支持基于部门的数据隔离，数据范围包括：\n" +
                                "- 全部数据\n" +
                                "- 本部门数据\n" +
                                "- 本部门及子部门数据\n" +
                                "- 仅本人数据")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EMC LIMS Team")
                                .email("emc-lims@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .paramName("Authorization")
                                        .description("请输入 JWT Token，格式：Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

    /**
     * 系统管理 API 分组
     */
    @Bean
    public GroupedOpenApi sysApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .packagesToScan("com.emclims.module.sys")
                .build();
    }

    /**
     * 认证 API 分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证接口")
                .packagesToScan("com.emclims.module.auth")
                .build();
    }

    /**
     * 全部 API（默认）
     * 使用 builder 替代 @Deprecated 的 addGroupedOpenApi
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("默认接口")
                .packagesToScan("com.emclims.module")
                .build();
    }
}
