package com.intern.hub.api.controller;

import com.intern.hub.api.dto.request.EvaluatorSubmissionCommentRequest;
import com.intern.hub.api.dto.request.EvaluatorSubmissionEvaluationRequest;
import com.intern.hub.api.dto.response.evaluator.EvaluatorCourseOverviewResponse;
import com.intern.hub.api.dto.response.submission.EvaluatorSubmissionOverviewResponse;
import com.intern.hub.api.dto.response.submission.SubmissionAttachmentResponse;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.domain.model.submission.constant.SubmissionEvaluationStatus;
import com.intern.hub.core.service.evaluator.EvaluatorService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import com.intern.hub.starter.security.annotation.HasPermission;
import com.intern.hub.starter.security.entity.Action;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/evaluator")
@Tag(name = "Evaluator", description = "API cho evaluator xem thong ke enrollment theo khoa hoc.")
public class EvaluatorController {

  EvaluatorService evaluatorService;

  @GetMapping("/courses")
  @Authenticated
  @HasPermission(resource = "danh-gia-bai-hoc", action = Action.READ)
  @Operation(
      summary = "Danh sach khoa hoc evaluator",
      description =
          "Lay danh sach khoa hoc kem so luong enrollment, hoan thanh, chua hoan thanh "
              + "va co the danh gia hay khong.")
  public ResponseApi<PaginatedData<EvaluatorCourseOverviewResponse>> getEvaluatorCourses(
      @RequestParam(value = "onlyEvaluable", required = false, defaultValue = "false")
          boolean onlyEvaluable,
      @PageableDefault(size = 10) Pageable pageable) {
    Long evaluatorUserId = UserContext.requiredUserId();
    var page = evaluatorService.getCourseOverviews(evaluatorUserId, onlyEvaluable, pageable);
    var result =
        PaginationUtils.toPaginatedData(
            page,
            item ->
                new EvaluatorCourseOverviewResponse(
                    item.getCourseId() == null ? null : item.getCourseId().toString(),
                    item.getName(),
                    item.getCourseImageUrl(),
                    item.getTotalEnrollmentCount(),
                    item.getCompletedEnrollmentCount(),
                    item.getNotCompletedEnrollmentCount(),
                    item.isCanEvaluate()));
    return ResponseApi.ok(result);
  }

  @GetMapping("/courses/{courseId}/submissions")
  @Authenticated
  @HasPermission(resource = "danh-gia-bai-hoc", action = Action.READ)
  @Operation(
      summary = "Danh sach bai nop cua khoa hoc",
      description = "Lay danh sach bai nop trong khoa hoc ma evaluator duoc phan cong.")
  public ResponseApi<List<EvaluatorSubmissionOverviewResponse>> getCourseSubmissions(
      @PathVariable("courseId") String courseId) {
    Long evaluatorUserId = UserContext.requiredUserId();
    var result =
        evaluatorService.getCourseSubmissions(parseId(courseId, "courseId"), evaluatorUserId);
    return ResponseApi.ok(result.stream().map(this::toSubmissionResponse).toList());
  }

  @PostMapping("/submissions/{lessonSubmissionId}/comments")
  @Authenticated
  @HasPermission(resource = "danh-gia-bai-hoc", action = Action.REVIEW)
  @Operation(
      summary = "Gui nhan xet cho bai nop",
      description = "Evaluator gui them nhan xet cho bai nop thuoc khoa hoc duoc phan cong.")
  public ResponseApi<?> commentSubmission(
      @PathVariable("lessonSubmissionId") String lessonSubmissionId,
      @Valid @RequestBody EvaluatorSubmissionCommentRequest request) {
    evaluatorService.commentSubmission(
        parseId(lessonSubmissionId, "lessonSubmissionId"),
        UserContext.requiredUserId(),
        request.comment());
    return ResponseApi.noContent();
  }

  @PostMapping("/submissions/{lessonSubmissionId}/evaluation-status")
  @Authenticated
  @HasPermission(resource = "danh-gia-bai-hoc", action = Action.REVIEW)
  @Operation(
      summary = "Cap nhat trang thai duyet bai nop",
      description = "Evaluator cap nhat trang thai bai nop sang PENDING, APPROVED hoac REJECTED.")
  public ResponseApi<?> updateEvaluationStatus(
      @PathVariable("lessonSubmissionId") String lessonSubmissionId,
      @Valid @RequestBody EvaluatorSubmissionEvaluationRequest request) {
    evaluatorService.evaluateSubmission(
        parseId(lessonSubmissionId, "lessonSubmissionId"),
        UserContext.requiredUserId(),
        parseEvaluationStatus(request.evaluationStatus()));
    return ResponseApi.noContent();
  }

  private EvaluatorSubmissionOverviewResponse toSubmissionResponse(
      com.intern.hub.core.domain.model.submission.EvaluatorSubmissionOverviewModel item) {
    var attachments =
        item.getAttachments() == null
            ? List.<SubmissionAttachmentResponse>of()
            : item.getAttachments().stream()
                .map(
                    attachment ->
                        new SubmissionAttachmentResponse(
                            attachment.getSubmissionAttachmentId() == null
                                ? null
                                : attachment.getSubmissionAttachmentId().toString(),
                            attachment.getFileName(),
                            attachment.getFileUrl(),
                            attachment.getFileSize()))
                .toList();
    return new EvaluatorSubmissionOverviewResponse(
        item.getLessonSubmissionId() == null ? null : item.getLessonSubmissionId().toString(),
        item.getCourseEnrollmentId() == null ? null : item.getCourseEnrollmentId().toString(),
        item.getLessonEnrollmentId() == null ? null : item.getLessonEnrollmentId().toString(),
        item.getLessonId() == null ? null : item.getLessonId().toString(),
        item.getLessonName(),
        item.getUserId() == null ? null : item.getUserId().toString(),
        item.getUserEmail(),
        item.getUserFullName(),
        item.getUserAvatarUrl(),
        item.getSubmissionStatus() == null ? null : item.getSubmissionStatus().name(),
        item.getEvaluationStatus() == null ? null : item.getEvaluationStatus().name(),
        item.getLastSubmissionAt(),
        item.getLearnerNote(),
        item.getEvaluatorComment(),
        attachments);
  }

  private SubmissionEvaluationStatus parseEvaluationStatus(String value) {
    if (value == null || value.isBlank()) {
      throw new BadRequestException(
          "submission.evaluation.invalid", "Trang thai danh gia khong hop le");
    }
    try {
      return SubmissionEvaluationStatus.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(
          "submission.evaluation.invalid", "Trang thai danh gia khong hop le");
    }
  }

  private Long parseId(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new BadRequestException("id.invalid", field + " khong hop le");
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new BadRequestException("id.invalid", field + " khong hop le");
    }
  }
}
