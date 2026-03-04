package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.course.CourseDetailResponse;
import com.intern.hub.api.dto.response.course.CourseSummaryResponse;
import com.intern.hub.api.dto.response.lesson.LessonSummaryResponse;
import com.intern.hub.api.mapper.CourseApiMapper;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.core.service.course.CourseService;
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
    LessonService lessonService;
    CourseApiMapper courseApiMapper;
    LessonApiMapper lessonApiMapper;

    @GetMapping
    @Authenticated
    @Operation(summary = "Danh sách khóa học", description = "Lấy danh sách khóa học có phân trang.")
    public ResponseApi<PaginatedData<CourseSummaryResponse>> getCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        var page = courseService.getCourses(pageable);
        var res = PaginationUtils.toPaginatedData(page, courseApiMapper::toSummaryResponse);
        return ResponseApi.ok(res);
    }

    @GetMapping("/{courseId}")
    @Authenticated
    @Operation(summary = "Chi tiết khóa học", description = "Lấy chi tiết khóa học theo id.")
    public ResponseApi<CourseDetailResponse> getCourse(@PathVariable("courseId") String courseId) {
        Long courseIdValue = parseId(courseId, "courseId");
        var model = courseService.getCourse(courseIdValue);
        var lessons =
                courseService.getCourseLessons(courseIdValue).stream()
                        .map(item -> lessonApiMapper.toSummaryResponse(item, null))
                        .toList();
        var courseIdString = model.getCourseId() == null ? null : model.getCourseId().toString();
        var res =
                new CourseDetailResponse(
                        courseIdString,
                        model.getName(),
                        model.getDescription(),
                        model.getCourseImageUrl(),
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
}
