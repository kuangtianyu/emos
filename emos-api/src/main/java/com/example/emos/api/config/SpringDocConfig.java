package com.example.emos.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: kty
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "emos-api",
                description = "Emos管理系统后端Java项目",
                version = "2.0"
        )
)

@SecurityScheme(
        name = "token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)

public class SpringDocConfig {


}