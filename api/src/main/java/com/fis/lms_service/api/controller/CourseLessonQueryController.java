package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.api.util.PaginationUtils;
import com.fis.lms_service.core.service.lesson.LessonQueryService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/courses")
public class CourseLessonQueryController {

  LessonQueryService lessonQueryService;
  LessonApiMapper lessonApiMapper;

  @GetMapping("/{courseId}/lessons")
  public ResponseApi<PaginatedData<LessonSummaryResponse>> getCourseLessons(
      @PathVariable("courseId") String courseId,
      @RequestParam(value = "userId", required = false) String userId,
      @PageableDefault() Pageable pageable) {
    Long courseIdValue = parseId(courseId, "courseId");
    Long userIdValue = parseOptionalId(userId);

    var page = lessonQueryService.getLessonsByCourse(courseIdValue, pageable);
    var res =
        PaginationUtils.toPaginatedData(
            page,
            model ->
                lessonApiMapper.toSummaryResponse(
                    model,
                    lessonQueryService.getLessonEnrollmentId(model.getLessonId(), userIdValue)));

    return ResponseApi.ok(res);
  }

  private Long parseId(String value, String field) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new com.intern.hub.library.common.exception.BadRequestException(
          "id.invalid", field + " không hợp lệ");
    }
  }

  private Long parseOptionalId(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return parseId(value, "userId");
  }
}
