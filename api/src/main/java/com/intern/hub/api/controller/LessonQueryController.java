package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.lesson.LessonDetailResponse;
import com.intern.hub.api.dto.response.lesson.LessonFileInfoResponse;
import com.intern.hub.api.dto.response.lesson.LessonSummaryResponse;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.service.lesson.LessonFileService;
import com.intern.hub.core.service.lesson.LessonQueryService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/lessons")
@Tag(name = "Lesson Query", description = "Tra cứu bài học toàn hệ thống.")
public class LessonQueryController {

  LessonQueryService lessonQueryService;
  LessonFileService lessonFileService;
  LessonApiMapper lessonApiMapper;

  @GetMapping
  @Authenticated
  @Operation(
      summary = "Danh sách bài học",
      description = "Lấy danh sách bài học có phân trang, hỗ trợ userId để map lessonEnrollmentId.")
  public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
      @PageableDefault(size = 10) Pageable pageable,
      @RequestParam(value = "userId", required = false) String userId) {
    var lessonPage = lessonQueryService.getLessons(pageable);

    Long userIdValue = parseOptionalId(userId, "userId");
    var res =
        PaginationUtils.toPaginatedData(
            lessonPage,
            model ->
                lessonApiMapper.toSummaryResponse(
                    model,
                    lessonQueryService.getLessonEnrollmentId(model.getLessonId(), userIdValue)));

    return ResponseApi.ok(res);
  }

  @GetMapping("/{lessonId}")
  @Authenticated
  @Operation(
      summary = "Chi tiết bài học",
      description = "Lấy chi tiết bài học theo id, kèm file và lessonEnrollmentId (nếu có userId).")
  public ResponseApi<LessonDetailResponse> getLessonDetail(
      @PathVariable("lessonId") String lessonId,
      @RequestParam(value = "userId", required = false) String userId) {
    Long lessonIdValue = parseId(lessonId, "lessonId");
    LessonModel model = lessonQueryService.getLesson(lessonIdValue);
    var fileModels = lessonFileService.getFiles(lessonIdValue);

    List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
    Long lessonEnrollmentId =
        lessonQueryService.getLessonEnrollmentId(lessonIdValue, parseOptionalId(userId, "userId"));
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
      throw new BadRequestException("id.invalid", field + " không hợp lệ");
    }
  }

  private Long parseId(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new BadRequestException("id.invalid", field + " không hợp lệ");
    }
    return parseOptionalId(value, field);
  }
}
