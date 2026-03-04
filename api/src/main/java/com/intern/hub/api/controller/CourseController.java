package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.course.CourseUserDetailResponse;
import com.intern.hub.api.dto.response.course.CourseUserSummaryResponse;
import com.intern.hub.api.dto.response.lesson.LessonSummaryResponse;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.service.course.CourseService;
import com.intern.hub.core.service.enrollment.EnrollmentService;
import com.intern.hub.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/courses")
@Tag(name = "Course", description = "Tra cứu khóa học và danh sách bài học theo khóa học.")
public class CourseController {

    CourseService courseService;
    EnrollmentService enrollmentService;
    LessonService lessonService;
    LessonApiMapper lessonApiMapper;

    @GetMapping
    @Authenticated
    @Operation(summary = "Danh sách khóa học", description = "Lấy danh sách khóa học có phân trang.")
    public ResponseApi<PaginatedData<CourseUserSummaryResponse>> getCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = UserContext.requiredUserId();
        var page = courseService.getCourses(pageable);
        var res = PaginationUtils.toPaginatedData(
                page,
                model -> toCourseUserSummaryResponse(
                        model, enrollmentService
                                .getCourseEnrollment(model.getCourseId(), userId)
                                .orElse(null)
                ));

        return ResponseApi.ok(res);
    }

    @GetMapping("/{courseId}")
    @Authenticated
    @Operation(summary = "Chi tiết khóa học", description = "Lấy chi tiết khóa học theo id.")
    public ResponseApi<CourseUserDetailResponse> getCourse(@PathVariable("courseId") String courseId) {
        Long courseIdValue = parseId(courseId, "courseId");
        Long userId = UserContext.requiredUserId();
        var model = courseService.getCourse(courseIdValue);
        var enrollment = enrollmentService.getCourseEnrollment(courseIdValue, userId).orElse(null);
        var lessons =
                courseService.getCourseLessons(courseIdValue).stream()
                        .map(item -> lessonApiMapper.toSummaryResponse(item, null))
                        .toList();
        var courseIdString = model.getCourseId() == null ? null : model.getCourseId().toString();
        var res =
                new CourseUserDetailResponse(
                        courseIdString,
                        model.getName(),
                        model.getDescription(),
                        model.getCourseImageUrl(),
                        enrollment != null,
                        enrollment == null || enrollment.getCourseEnrollmentId() == null
                                ? null
                                : enrollment.getCourseEnrollmentId().toString(),
                        enrollment == null || enrollment.getCourseProgress() == null
                                ? null
                                : enrollment.getCourseProgress().name(),
                        lessons);
        return ResponseApi.ok(res);
    }

    @GetMapping("/{courseId}/lessons")
    @Authenticated
    @Operation(
            summary = "Danh sách lesson theo khóa học",
            description = "Trả danh sách lesson của course; có thể kèm lessonEnrollmentId theo userId.")
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getCourseLessons(
            @PathVariable("courseId") String courseId,
            @RequestParam(value = "userId", required = false) String userId,
            @PageableDefault Pageable pageable) {
        Long courseIdValue = parseId(courseId, "courseId");
        Long userIdValue = parseOptionalId(userId, "userId");

        var page = courseService.getCourseLessons(courseIdValue, pageable);
        var res =
                PaginationUtils.toPaginatedData(
                        page,
                        model ->
                                lessonApiMapper.toSummaryResponse(
                                        model,
                                        lessonService.getLessonEnrollmentId(model.getLessonId(), userIdValue)));

        return ResponseApi.ok(res);
    }

    private Long parseOptionalId(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseId(value, field);
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

    private CourseUserSummaryResponse toCourseUserSummaryResponse(
            com.intern.hub.core.domain.model.course.CourseModel model,
            com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel enrollment) {
        return new CourseUserSummaryResponse(
                model.getCourseId() == null ? null : model.getCourseId().toString(),
                model.getName(),
                model.getCourseImageUrl(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                enrollment != null,
                enrollment == null || enrollment.getCourseEnrollmentId() == null
                        ? null
                        : enrollment.getCourseEnrollmentId().toString(),
                enrollment == null || enrollment.getCourseProgress() == null
                        ? null
                        : enrollment.getCourseProgress().name());
    }
}
