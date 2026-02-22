package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.LessonFileService;
import com.fis.lms_service.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lessons")
public class LessonQueryController {

  LessonService lessonService;
  LessonFileService lessonFileService;
  LessonApiMapper lessonApiMapper;

  @GetMapping
  public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
      @PageableDefault(size = 10) Pageable pageable,
      @RequestParam(value = "userId", required = false) String userId) {
    var lessonPage = lessonService.getLessons(pageable);

    Long userIdValue = parseOptionalId(userId, "userId");
    var items =
        lessonPage.getContent().stream()
            .map(
                model ->
                    lessonApiMapper.toSummaryResponse(
                        model,
                        lessonService.getLessonEnrollmentId(
                            model.getLessonId(), userIdValue)))
            .toList();

    var res =
        PaginatedData.<LessonSummaryResponse>builder()
            .items(items)
            .totalItems(lessonPage.getTotalElements())
            .totalPages(lessonPage.getTotalPages())
            .build();

    return ResponseApi.ok(res);
  }

  @GetMapping("/{lessonId}")
  public ResponseApi<LessonDetailResponse> getLessonDetail(
      @PathVariable("lessonId") String lessonId,
      @RequestParam(value = "userId", required = false) String userId) {
    Long lessonIdValue = parseId(lessonId, "lessonId");
    LessonModel model = lessonService.getLesson(lessonIdValue);
    var fileModels = lessonFileService.getFiles(lessonIdValue);

    List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
    Long lessonEnrollmentId =
        lessonService.getLessonEnrollmentId(lessonIdValue, parseOptionalId(userId, "userId"));
    LessonDetailResponse res = lessonApiMapper.toDetailResponse(model, files, lessonEnrollmentId);

    return ResponseApi.ok(res);
  }

  private Long parseOptionalId(String value, String field) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new com.intern.hub.library.common.exception.BadRequestException(
          "id.invalid", field + " không hợp lệ");
    }
  }

  private Long parseId(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new com.intern.hub.library.common.exception.BadRequestException(
          "id.invalid", field + " không hợp lệ");
    }
    return parseOptionalId(value, field);
  }
}
