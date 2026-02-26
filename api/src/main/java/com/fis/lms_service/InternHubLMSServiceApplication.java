package com.fis.lms_service;

import com.intern.hub.library.common.annotation.EnableGlobalExceptionHandler;
import com.intern.hub.starter.security.annotation.EnableSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableSecurity
@EnableGlobalExceptionHandler
public class InternHubLMSServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(InternHubLMSServiceApplication.class, args);
  }
}
