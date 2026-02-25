package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.CourseCreateRequest;
import com.fis.lms_service.api.dto.response.course.CourseDetailResponse;
import com.fis.lms_service.api.dto.response.course.CourseSummaryResponse;
import com.fis.lms_service.api.mapper.CourseApiMapper;
import com.fis.lms_service.api.util.PaginationUtils;
import com.fis.lms_service.core.service.course.AdminCourseService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/admin/courses")
@Tag(name = "Admin Course", description = "Quản trị khóa học: CRUD và gắn lesson theo danh sách id.")
public class AdminCourseController {

    AdminCourseService adminCourseService;
    CourseApiMapper courseApiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Tạo khóa học", description = "Tạo khóa học mới (multipart: data + image).")
    public ResponseApi<?> createCourse(
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image) {

        adminCourseService.createCourse(
                courseApiMapper.toModel(request), image, parseLessonIds(request.lessonIds()));
        return ResponseApi.noContent();
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
        if (value == null || value.isBlank()) {
            throw new BadRequestException("id.invalid", field + " không hợp lệ");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("id.invalid", field + " không hợp lệ");
        }
    }

    @GetMapping
    @Operation(summary = "Danh sách khóa học", description = "Lấy danh sách khóa học có phân trang.")
    public ResponseApi<PaginatedData<CourseSummaryResponse>> getCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        var page = adminCourseService.getCourses(pageable);
        var res = PaginationUtils.toPaginatedData(page, courseApiMapper::toSummaryResponse);
        return ResponseApi.ok(res);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Chi tiết khóa học", description = "Lấy chi tiết khóa học theo id.")
    public ResponseApi<CourseDetailResponse> getCourse(@PathVariable("courseId") String courseId) {
        Long courseIdValue = parseId(courseId, "courseId");
        var model = adminCourseService.getCourse(courseIdValue);
        var lessonIds =
                adminCourseService.getCourseLessonIds(courseIdValue).stream().map(String::valueOf).toList();
        var courseIdString = model.getCourseId() == null ? null : model.getCourseId().toString();
        var res =
                new CourseDetailResponse(
                        courseIdString,
                        model.getName(),
                        model.getDescription(),
                        model.getCourseImageUrl(),
                        lessonIds);
        return ResponseApi.ok(res);
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cập nhật khóa học", description = "Cập nhật thông tin khóa học, có thể thay ảnh.")
    public ResponseApi<?> updateCourse(
            @PathVariable("courseId") String courseId,
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        adminCourseService.updateCourse(
                parseId(courseId, "courseId"), courseApiMapper.toModel(request), image);
        return ResponseApi.noContent();
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Xóa khóa học", description = "Xóa khóa học theo id.")
    public ResponseApi<?> deleteCourse(@PathVariable("courseId") String courseId) {
        adminCourseService.deleteCourse(parseId(courseId, "courseId"));
        return ResponseApi.noContent();
    }
}
