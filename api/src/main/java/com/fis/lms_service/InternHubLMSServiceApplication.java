package com.fis.lms_service;

import com.intern.hub.library.common.annotation.EnableGlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableGlobalExceptionHandler
public class InternHubLMSServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(InternHubLMSServiceApplication.class, args);
    }
}
