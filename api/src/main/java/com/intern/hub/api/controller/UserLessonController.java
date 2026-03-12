package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.lesson.LessonFileInfoResponse;
import com.intern.hub.api.dto.response.lesson.LessonUserDetailResponse;
import com.intern.hub.api.dto.response.lesson.LessonUserSummaryResponse;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.service.enrollment.EnrollmentService;
import com.intern.hub.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/lessons")
@Tag(name = "User Lesson", description = "API người dùng để tra cứu bài học và tiến độ học.")
public class UserLessonController {

  EnrollmentService enrollmentService;
  LessonService lessonService;
  LessonApiMapper lessonApiMapper;

  @GetMapping
  @Authenticated
  @Operation(
      summary = "Danh sách bài học của người dùng",
      description =
          "Lấy danh sách bài học có phân trang, kèm trạng thái enrollment của user hiện tại.")
  public ResponseApi<PaginatedData<LessonUserSummaryResponse>> getLessons(
      @PageableDefault(size = 10) Pageable pageable) {
    var lessonPage = lessonService.getLessons(pageable);
    Long userIdValue = UserContext.requiredUserId();

    var res =
        PaginationUtils.toPaginatedData(
            lessonPage,
            model ->
                toLessonUserSummaryResponse(
                    model,
                    lessonService.getLessonEnrollment(model.getLessonId(), userIdValue).orElse(null)));

    return ResponseApi.ok(res);
  }

  @GetMapping("/{lessonId}")
  @Authenticated
  @Operation(
      summary = "Chi tiết bài học của người dùng",
      description =
          "Lấy chi tiết bài học theo id, kèm file và trạng thái enrollment của user hiện tại.")
  public ResponseApi<LessonUserDetailResponse> getLessonDetail(
      @PathVariable("lessonId") String lessonId) {
    Long lessonIdValue = parseId(lessonId, "lessonId");
    Long userIdValue = UserContext.requiredUserId();
    LessonModel model = lessonService.getLesson(lessonIdValue);
    var fileModels = lessonService.getLessonFiles(lessonIdValue);
    LessonEnrollmentModel enrollment =
        lessonService.getLessonEnrollment(lessonIdValue, userIdValue).orElse(null);

    List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
    LessonUserDetailResponse res = toLessonUserDetailResponse(model, files, enrollment);

    return ResponseApi.ok(res);
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

  private LessonUserSummaryResponse toLessonUserSummaryResponse(
      LessonModel model, LessonEnrollmentModel enrollment) {
    return new LessonUserSummaryResponse(
        model.getLessonId() == null ? null : model.getLessonId().toString(),
        model.getName(),
        model.getLessonImageUrl(),
        enrollment == null || enrollment.getLessonEnrollmentId() == null
            ? null
            : enrollment.getLessonEnrollmentId().toString(),
        enrollment == null || enrollment.getCourseEnrollmentId() == null
            ? null
            : enrollment.getCourseEnrollmentId().toString(),
        enrollment == null || enrollment.getLessonProgress() == null
            ? null
            : enrollment.getLessonProgress().name(),
        model.getCreatedAt(),
        model.getUpdatedAt());
  }

  private LessonUserDetailResponse toLessonUserDetailResponse(
      LessonModel model, List<LessonFileInfoResponse> files, LessonEnrollmentModel enrollment) {
    return new LessonUserDetailResponse(
        model.getLessonId() == null ? null : model.getLessonId().toString(),
        model.getName(),
        model.getIntroduction(),
        model.getContent(),
        model.getRequirements(),
        model.getLessonImageUrl(),
        files,
        enrollment == null || enrollment.getLessonEnrollmentId() == null
            ? null
            : enrollment.getLessonEnrollmentId().toString(),
        enrollment == null || enrollment.getCourseEnrollmentId() == null
            ? null
            : enrollment.getCourseEnrollmentId().toString(),
        enrollment == null || enrollment.getLessonProgress() == null
            ? null
            : enrollment.getLessonProgress().name());
  }
}
