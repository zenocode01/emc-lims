package com.emclims.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

/**
 * Knife4j (OpenAPI) 配置
 */
@Configuration
public class Knife4jConfig {

    /**
     * 默认 API 分组
     */
    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("默认接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emclims.module"))
                .build()
                .securitySchemes(Arrays.asList(securityScheme()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    /**
     * 系统管理 API 分组
     */
    @Bean
    public Docket sysApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("系统管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emclims.module.sys"))
                .build()
                .securitySchemes(Arrays.asList(securityScheme()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    /**
     * 认证 API 分组
     */
    @Bean
    public Docket authApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("认证接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emclims.module.auth"))
                .build();
    }

    /**
     * API 文档信息
     */
    private Info apiInfo() {
        return new Info()
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
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    /**
     * 安全方案 - JWT Token
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .paramName("Authorization")
                .description("请输入 JWT Token，格式：Bearer {token}");
    }

    /**
     * 安全上下文
     */
    private io.swagger.v3.oas.models.security.SecurityContext securityContext() {
        return new io.swagger.v3.oas.models.security.SecurityContext()
                .security(Arrays.asList(new SecurityRequirement().addList("Authorization")));
    }
}
