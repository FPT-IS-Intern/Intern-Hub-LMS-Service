package com.fis.lms_service.api.config;

import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/** Admin 1/25/2026 */
@Configuration(proxyBeanMethods = false)
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<@NonNull String> auditorAware() {
    return () ->
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(
                p -> {
                  if (p instanceof UserDetails ud) return ud.getUsername();
                  if (p instanceof String s && !s.isBlank()) return s;
                  return null;
                })
            .or(
                () ->
                    Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .map(Authentication::getName))
            .filter(s -> !s.isBlank())
            .or(() -> Optional.of("system"));
  }
}
