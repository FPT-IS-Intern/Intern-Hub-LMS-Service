package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.CourseEnrollRequest;
import com.fis.lms_service.core.service.course.CourseService;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
public class CourseEnrollmentController {

  CourseService courseService;

  @PostMapping("/{courseId}/enroll")
  public ResponseApi<Boolean> enrollCourse(
      @PathVariable("courseId") Long courseId, @RequestBody @Valid CourseEnrollRequest request) {
    courseService.enrollCourse(courseId, request.userId());
    return ResponseApi.ok(true);
  }
}
