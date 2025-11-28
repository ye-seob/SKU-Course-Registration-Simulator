package com.v1.skuproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 액세스 토큰을 입력하세요.")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                .info(new Info()
                        .title("SKU-Course-Registration-Simulator API")
                        .description("서경대 수강신청 시뮬레이터 API 문서")
                        .version("v1.0")
                )

                .externalDocs(new ExternalDocumentation()
                        .description("프로젝트 GitHub")
                        .url("https://github.com/ye-seob/SKU-Course-Registration-Simulator")
                );
    }
}