package com.flowerd.backend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    // 명세서 생성
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlowERD API")
                        .version("1.0")
                        .description("FlowERD API 명세서입니다."));
    }

    @Bean
    // 그룹화된 API 생성
    public GroupedOpenApi api() {
        String[] paths = {"/**"};
        String[] packagesToScan = {"com.flowerd.backend"};
        return GroupedOpenApi.builder()
                .group("FlowERD API")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .build();
    }
}
