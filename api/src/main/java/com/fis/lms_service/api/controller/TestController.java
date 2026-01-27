package com.fis.lms_service.api.controller;

import com.fis.lms_service.core.TestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Admin 1/24/2026 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {

  TestService testService;

  @GetMapping("/test")
  public String test(@RequestParam("name") String name) {
    return testService.processRequest(name);
  }
}
