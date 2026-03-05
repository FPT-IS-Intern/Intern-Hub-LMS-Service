package com.intern.hub.api.controller;

import com.intern.hub.api.dto.request.CourseCreateRequest;
import com.intern.hub.api.dto.response.course.CourseEvaluatorCandidateResponse;
import com.intern.hub.api.dto.response.course.CourseDetailResponse;
import com.intern.hub.api.dto.response.course.CourseEvaluatorUserResponse;
import com.intern.hub.api.dto.response.course.CourseSummaryResponse;
import com.intern.hub.api.mapper.CourseApiMapper;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.core.service.course.AdminCourseService;
import com.intern.hub.core.service.course.CourseService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/admin/courses")
@Tag(
        name = "Admin Course",
        description = "Quản trị khóa học: CRUD và gắn lesson theo danh sách id.")
public class AdminCourseController {

    AdminCourseService adminCourseService;
    CourseService courseService;
    UserDirectoryRepository userDirectoryRepository;
    CourseApiMapper courseApiMapper;
    LessonApiMapper lessonApiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Authenticated
    @Operation(summary = "Tạo khóa học", description = "Tạo khóa học mới (multipart: data + image).")
    public ResponseApi<?> createCourse(
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image) {

        adminCourseService.createCourse(
                courseApiMapper.toModel(request),
                image,
                parseLessonIds(request.lessonIds()),
                parseEvaluatorUserIds(request.evaluatorUserIds()),
                UserContext.requiredUserId());
        return ResponseApi.noContent();
    }

    @GetMapping
    @Authenticated
    @Operation(summary = "Danh sách khóa học", description = "Lấy danh sách khóa học có phân trang.")
    public ResponseApi<PaginatedData<CourseSummaryResponse>> getCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        var page = adminCourseService.getCourses(pageable);
        var res = PaginationUtils.toPaginatedData(page, courseApiMapper::toSummaryResponse);
        return ResponseApi.ok(res);
    }

  @GetMapping("/{courseId:\\d+}")
  @Authenticated
  @Operation(summary = "Chi tiết khóa học", description = "Lấy chi tiết khóa học theo id.")
  public ResponseApi<CourseDetailResponse> getCourse(@PathVariable("courseId") String courseId) {
        Long courseIdValue = parseId(courseId, "courseId");
        var model = adminCourseService.getCourse(courseIdValue);
        var lessons =
                courseService.getCourseLessons(courseIdValue).stream()
                        .map(lessonApiMapper::toSummaryResponse)
                        .toList();
        var evaluatorUserIds = adminCourseService.getCourseEvaluatorUserIds(courseIdValue);
        var evaluators = userDirectoryRepository.findByIds(evaluatorUserIds).stream()
                .map(user ->
                        new CourseEvaluatorUserResponse(
                                user.getUserId() == null ? null : user.getUserId().toString(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getRole(),
                                user.getAvatarUrl()))
                .toList();
        var courseIdString = model.getCourseId() == null ? null : model.getCourseId().toString();
        var res =
                new CourseDetailResponse(
                        courseIdString,
                        model.getName(),
                        model.getDescription(),
                        model.getCourseImageUrl(),
                        lessons,
                        evaluators);
        return ResponseApi.ok(res);
    }

  @PutMapping(value = "/{courseId:\\d+}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Authenticated
  @Operation(
      summary = "Cập nhật khóa học",
            description = "Cập nhật thông tin khóa học, có thể thay ảnh.")
    public ResponseApi<?> updateCourse(
            @PathVariable("courseId") String courseId,
            @RequestPart("data") @Valid CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        adminCourseService.updateCourse(
                parseId(courseId, "courseId"),
                courseApiMapper.toModel(request),
                image,
                parseLessonIdsForUpdate(request.lessonIds()),
                UserContext.requiredUserId());
        return ResponseApi.noContent();
    }

  @DeleteMapping("/{courseId:\\d+}")
  @Authenticated
  @Operation(summary = "Xóa khóa học", description = "Xóa khóa học theo id.")
  public ResponseApi<?> deleteCourse(@PathVariable("courseId") String courseId) {
        adminCourseService.deleteCourse(parseId(courseId, "courseId"), UserContext.requiredUserId());
        return ResponseApi.noContent();
    }

    @GetMapping("/evaluator-candidate")
    @Authenticated
    @Operation(
            summary = "Tra cứu người dùng theo email",
            description =
                    "Tra cứu thông tin user theo email để hiển thị candidate evaluator trước khi thực hiện add vào course.")
    public ResponseApi<CourseEvaluatorCandidateResponse> getEvaluatorCandidateByEmail(
            @RequestParam("email") String email
    ) {
        if (email == null || email.isBlank())
            throw new BadRequestException("email.invalid", "email không hợp lệ");

        var candidate = userDirectoryRepository
                .findByEmail(email)
                .map(user ->
                        new CourseEvaluatorCandidateResponse(
                                user.getUserId() == null ? null : user.getUserId().toString(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getRole(),
                                user.getAvatarUrl())
                )
                .orElse(null);

        return ResponseApi.ok(candidate);
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

    private List<Long> parseLessonIdsForUpdate(List<String> lessonIds) {
        if (lessonIds == null) {
            return null;
        }
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        return parseLessonIds(lessonIds);
    }

    private List<Long> parseEvaluatorUserIds(List<String> evaluatorUserIds) {
        if (evaluatorUserIds == null || evaluatorUserIds.isEmpty()) {
            return null;
        }
        return evaluatorUserIds.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> parseId(value, "evaluatorUserIds"))
                .toList();
    }
}
