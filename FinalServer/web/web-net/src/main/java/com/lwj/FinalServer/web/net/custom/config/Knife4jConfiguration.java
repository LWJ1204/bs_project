package com.lwj.FinalServer.web.net.custom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(
                new Info()
                        .title("考勤后台管理系统")
                        .version("1.0")
                        .description("考勤管理api")
        );
    }

    @Bean
    public GroupedOpenApi HomeAPI() {

        return GroupedOpenApi.builder().group("首页学院管理").
                pathsToMatch(
                        "/api/home/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi permissionAPI() {

        return GroupedOpenApi.builder().group("后台登录管理").
                pathsToMatch(
                        "/api/permission/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi recordAPI() {

        return GroupedOpenApi.builder().group("考勤记录管理").
                pathsToMatch(
                        "/api/record/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi courseAPI() {
        return GroupedOpenApi.builder().group("课程信息管理").
                pathsToMatch(
                        "/api/course/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi classAPI() {
        return GroupedOpenApi.builder().group("教室管理").
                pathsToMatch(
                        "/api/class/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi teacherAPI() {
        return GroupedOpenApi.builder().group("教师管理").
                pathsToMatch(
                        "/api/teacher/**"
                ).build();
    }


}

