package com.intern.hub.api.controller;

import com.intern.hub.api.dto.request.LessonSubmissionRequest;
import com.intern.hub.api.dto.response.submission.LessonSubmissionResponse;
import com.intern.hub.api.dto.response.submission.SubmissionAttachmentResponse;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.service.submission.LessonSubmissionService;
import com.intern.hub.core.service.submission.SubmissionService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/lesson-enrollments")
@Tag(
    name = "User Submission",
    description = "API người dùng để xem và nộp bài theo lesson enrollment.")
public class UserSubmissionController {

  SubmissionService submissionService;

  @GetMapping("/{lessonEnrollmentId}/submissions")
  @Authenticated
  @Operation(
      summary = "Chi tiết bài nộp của tôi",
      description = "Lấy bài nộp hiện tại theo lesson enrollment id.")
  public ResponseApi<LessonSubmissionResponse> getSubmission(
      @PathVariable("lessonEnrollmentId") String lessonEnrollmentId) {
    var result = submissionService.getSubmission(parseId(lessonEnrollmentId, "lessonEnrollmentId"));
    return ResponseApi.ok(toResponse(result));
  }

  @PostMapping(
      value = "/{lessonEnrollmentId}/submit",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Authenticated
  @Operation(
      summary = "Nộp bài cho người dùng",
      description =
          "Nộp hoặc cập nhật bài nộp của user hiện tại, hỗ trợ thêm file mới và xóa chọn lọc file cũ.")
  public ResponseApi<LessonSubmissionResponse> submitLesson(
      @PathVariable("lessonEnrollmentId") String lessonEnrollmentId,
      @RequestPart("data") @Valid LessonSubmissionRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    Long userId = UserContext.requiredUserId();
    var result =
        submissionService.submitLesson(
            parseId(lessonEnrollmentId, "lessonEnrollmentId"),
            userId,
            userId,
            request.comment(),
            parseIds(request.deleteAttachmentIds(), "deleteAttachmentIds"),
            files);
    return ResponseApi.ok(toResponse(result));
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

  private LessonSubmissionResponse toResponse(
      LessonSubmissionService.LessonSubmissionResult result) {
    var attachments =
        result.attachments().stream()
            .map(
                item ->
                    new SubmissionAttachmentResponse(
                        item.getSubmissionAttachmentId() == null
                            ? null
                            : item.getSubmissionAttachmentId().toString(),
                        item.getFileName(),
                        item.getFileUrl(),
                        item.getFileSize()))
            .toList();

    return new LessonSubmissionResponse(
        result.lessonSubmissionId() == null ? null : result.lessonSubmissionId().toString(),
        result.lessonEnrollmentId() == null ? null : result.lessonEnrollmentId().toString(),
        result.submissionStatus().name(),
        result.lastSubmissionAt(),
        result.comment(),
        attachments);
  }

  private List<Long> parseIds(List<String> values, String field) {
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.stream()
        .filter(value -> value != null && !value.isBlank())
        .map(value -> parseId(value, field))
        .toList();
  }
}
