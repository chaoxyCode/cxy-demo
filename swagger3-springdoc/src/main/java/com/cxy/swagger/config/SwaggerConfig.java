package com.cxy.swagger.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger3配置类-配置全局 API 文档信息
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@OpenAPIDefinition(
        // 全局文档配置
        tags = {
            @Tag(name = "用户", description = "用户模块"),
            @Tag(name = "账户", description = "账户模块"),
        },
        info =
                @Info(
                        title = "接口 API 文档",
                        description = "XXX系统文档",
                        version = "1.0.0",
                        contact =
                                @Contact(
                                        name = "chaoxy",
                                        email = "XXX@email.com",
                                        url = "https://www.111.com"),
                        license =
                                @License(
                                        name = "Apache 2.0",
                                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")),
        servers = {
            // 测试时访问地址
            @Server(description = "dev环境服务器", url = "http://127.0.0.1:8888/swagger/"),
            @Server(description = "sit环境服务器", url = "http://127.0.0.1:8888/swagger/"),
        },
        security = @SecurityRequirement(name = "Oauth2"),
        externalDocs =
                @ExternalDocumentation(
                        description = "项目编译部署说明",
                        url = "http://localhost/deploy/README.md"))
@Configuration
public class SwaggerConfig {

    /**
     * 设置全局 API 文档信息
     *
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部")
                .packagesToScan("com.cxy.swagger.controller")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 设置分组-并设置自定义的 API 文档信息
     *
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户")
                .packagesToScan("com.cxy.swagger.controller.usc")
                // 指定路径
                .pathsToMatch("/api/usc/**")
                // 指定特定的 API 文档信息
                .addOpenApiCustomiser(userApiCustom())
                .build();
    }

    /**
     * 设置分组-并设置自定义的 API 文档信息
     *
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder()
                .group("账户")
                .packagesToScan("com.cxy.swagger.controller.acc")
                .pathsToMatch("/api/acc/**")
                .addOpenApiCustomiser(accApiCustom())
                .build();
    }

    /**
     * 定义指定group文档信息
     *
     * @return OpenApiCustom
     */
    public OpenApiCustomiser userApiCustom() {
        return openApi ->
                openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("用户相关 API 文档")
                                .description("实现对用户信息相关处理")
                                .version("1.0")
                                .contact(
                                        new io.swagger.v3.oas.models.info.Contact()
                                                .name("chaoxy")
                                                .email("XXX@email.com")));
    }

    /**
     * 定义指定group文档信息
     *
     * @return OpenApiCustom
     */
    public OpenApiCustomiser accApiCustom() {
        return openApi ->
                openApi.info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("账户相关 API 文档")
                                .description("实现对用户账户信息相关处理")
                                .version("1.0")
                                .contact(
                                        new io.swagger.v3.oas.models.info.Contact()
                                                .name("chaoxy")
                                                .email("XXX@email.com")));
    }
}
