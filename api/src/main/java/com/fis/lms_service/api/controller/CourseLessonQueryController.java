package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.ResponseApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/courses")
public class CourseLessonQueryController {

  LessonService lessonService;
  LessonApiMapper lessonApiMapper;

  @GetMapping("/{courseId}/lessons")
  public ResponseApi<List<LessonSummaryResponse>> getCourseLessons(
      @PathVariable("courseId") String courseId,
      @RequestParam(value = "userId", required = false) String userId) {
    Long courseIdValue = parseId(courseId, "courseId");
    Long userIdValue = parseOptionalId(userId, "userId");

    var lessons = lessonService.getLessonsByCourse(courseIdValue);
    var items =
        lessons.stream()
            .map(
                model ->
                    lessonApiMapper.toSummaryResponse(
                        model,
                        lessonService.getLessonEnrollmentId(
                            model.getLessonId(), userIdValue)))
            .toList();

    return ResponseApi.ok(items);
  }

  private Long parseId(String value, String field) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new com.intern.hub.library.common.exception.BadRequestException(
          "id.invalid", field + " không hợp lệ");
    }
  }

  private Long parseOptionalId(String value, String field) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return parseId(value, field);
  }
}
