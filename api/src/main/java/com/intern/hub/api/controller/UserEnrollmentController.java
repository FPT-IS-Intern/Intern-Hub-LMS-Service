package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.enrollment.CourseEnrollmentResponse;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.service.enrollment.EnrollmentService;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping
@Tag(name = "User Enrollment", description = "API người dùng để tra cứu và ghi danh khóa học.")
public class UserEnrollmentController {

  EnrollmentService enrollmentService;

  @GetMapping("/lms/courses/{courseId}/enrollment")
  @Authenticated
  @Operation(
      summary = "Tra cứu ghi danh khóa học của tôi",
      description = "Lấy course enrollment theo courseId của user hiện tại.")
    public ResponseApi<CourseEnrollmentResponse> getCourseEnrollment(
            @PathVariable("courseId") String courseId) {
        var enrollment =
                enrollmentService.getCourseEnrollment(
                        parseId(courseId, "courseId"), UserContext.requiredUserId());
        return ResponseApi.ok(enrollment.map(this::toCourseEnrollmentResponse).orElse(null));
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

    private CourseEnrollmentResponse toCourseEnrollmentResponse(
            com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel model) {
        return new CourseEnrollmentResponse(
                model.getCourseEnrollmentId() == null ? null : model.getCourseEnrollmentId().toString(),
                model.getCourseId() == null ? null : model.getCourseId().toString(),
                model.getUserId() == null ? null : model.getUserId().toString(),
                model.getCourseProgress() == null ? null : model.getCourseProgress().name());
    }
}
