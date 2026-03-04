package com.intern.hub.api.controller;

import com.intern.hub.api.dto.request.CourseEnrollRequest;
import com.intern.hub.core.service.enrollment.EnrollmentService;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/lms/courses")
@Tag(name = "Enrollment", description = "Ghi danh khóa học cho user.")
public class EnrollmentController {

    EnrollmentService enrollmentService;

    @PostMapping("/{courseId}/enroll")
    @Authenticated
    @Operation(
            summary = "Ghi danh khóa học",
            description = "Tạo/cập nhật trạng thái ghi danh của user vào khóa học.")
    public ResponseApi<?> enrollCourse(
            @PathVariable("courseId") String courseId, @RequestBody @Valid CourseEnrollRequest request) {
        enrollmentService.enrollCourse(
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
