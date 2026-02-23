package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.CourseCreateRequest;
import com.fis.lms_service.api.dto.response.course.CourseDetailResponse;
import com.fis.lms_service.api.dto.response.course.CourseSummaryResponse;
import com.fis.lms_service.api.mapper.CourseApiMapper;
import com.fis.lms_service.api.util.PaginationUtils;
import com.fis.lms_service.core.service.course.CourseService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/courses")
public class AdminCourseController {

    CourseService courseService;
    CourseApiMapper courseApiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> createCourse(
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image) {

        courseService.createCourse(
                courseApiMapper.toModel(request), image, parseLessonIds(request.lessonIds()));
        return ResponseApi.ok(true);
    }

    private List<Long> parseLessonIds(List<String> lessonIds) {
        if (lessonIds == null || lessonIds.isEmpty()) {
            return null;
        }
        return lessonIds.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> parseId(value, "lessonIds"))
                .toList();
    }

    private Long parseId(String value, String field) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new com.intern.hub.library.common.exception.BadRequestException(
                    "id.invalid", field + " không hợp lệ");
        }
    }

    @GetMapping
    public ResponseApi<PaginatedData<CourseSummaryResponse>> getCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        var page = courseService.getCourses(pageable);
        var res = PaginationUtils.toPaginatedData(page, courseApiMapper::toSummaryResponse);
        return ResponseApi.ok(res);
    }

    @GetMapping("/{courseId}")
    public ResponseApi<CourseDetailResponse> getCourse(@PathVariable("courseId") String courseId) {
        Long courseIdValue = parseId(courseId, "courseId");
        var model = courseService.getCourse(courseIdValue);
        var lessonIds = courseService.getCourseLessonIds(courseIdValue).stream()
                .map(String::valueOf)
                .toList();
        var courseIdString = model.getCourseId() == null ? null : model.getCourseId().toString();
        var res = new CourseDetailResponse(
                courseIdString,
                model.getName(),
                model.getDescription(),
                model.getCourseImageUrl(),
                lessonIds);
        return ResponseApi.ok(res);
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> updateCourse(
            @PathVariable("courseId") String courseId,
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        courseService.updateCourse(
                parseId(courseId, "courseId"), courseApiMapper.toModel(request), image);
        return ResponseApi.ok(true);
    }

    @DeleteMapping("/{courseId}")
    public ResponseApi<Boolean> deleteCourse(@PathVariable("courseId") String courseId) {
        courseService.deleteCourse(parseId(courseId, "courseId"));
        return ResponseApi.ok(true);
    }
}
