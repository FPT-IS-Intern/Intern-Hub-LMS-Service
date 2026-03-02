package com.intern.hub.api.config;

import com.intern.hub.api.util.UserContext;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

/** Admin 1/25/2026 */
@Configuration(proxyBeanMethods = false)
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<@NonNull String> auditorAware() {
    return () -> UserContext.userId().map(String::valueOf).or(() -> Optional.of("system"));
  }
}
