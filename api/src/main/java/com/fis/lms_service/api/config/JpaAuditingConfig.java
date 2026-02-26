package com.fis.lms_service.api.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import com.fis.lms_service.api.util.UserContext;

import java.util.Optional;

/**
 * Admin 1/25/2026
 */
@Configuration(proxyBeanMethods = false)
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<@NonNull String> auditorAware() {
        return () -> UserContext.userId().map(String::valueOf).or(() -> Optional.of("system"));
    }
}
