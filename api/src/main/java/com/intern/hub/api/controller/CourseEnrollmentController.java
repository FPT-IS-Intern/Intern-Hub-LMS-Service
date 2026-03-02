package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.CourseEnrollRequest;
import com.fis.lms_service.core.service.course.CourseEnrollmentService;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/courses")
@Tag(name = "Course Enrollment", description = "Ghi danh khóa học cho user.")
public class CourseEnrollmentController {

  CourseEnrollmentService courseEnrollmentService;

  @PostMapping("/{courseId}/enroll")
  @Authenticated
  @Operation(
      summary = "Ghi danh khóa học",
      description = "Tạo/cập nhật trạng thái ghi danh của user vào khóa học.")
  public ResponseApi<?> enrollCourse(
      @PathVariable("courseId") String courseId, @RequestBody @Valid CourseEnrollRequest request) {
    courseEnrollmentService.enrollCourse(
        parseId(courseId, "courseId"), parseId(request.userId(), "userId"));
    return ResponseApi.noContent();
  }

  private Long parseId(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new BadRequestException("id.invalid", field + " không hợp lệ");
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new BadRequestException("id.invalid", field + " không hợp lệ");
    }
  }
}
