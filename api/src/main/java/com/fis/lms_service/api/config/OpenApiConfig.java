package com.fis.lms_service.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${gateway-url}")
    private String gatewayUrl;

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        // 1. Cấu hình xác thực bằng JWT (Bearer Token)
        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // 2. Cấu hình xác thực nội bộ (Internal Key)
        SecurityScheme internalApiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Internal-Secret");

        // 3. Customize OpenAPI: Thêm Server URL và Security Schemes
        return openApi -> openApi
                .addServersItem(new Server().url(gatewayUrl + "/api"))
                .components(
                        (openApi.getComponents() == null ? new Components() : openApi.getComponents())
                                .addSecuritySchemes("Bearer", bearerAuthScheme)
                                .addSecuritySchemes("InternalAPIKey", internalApiKeyScheme)
                );
    }
}