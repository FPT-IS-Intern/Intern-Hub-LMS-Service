package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonSubmissionRequest;
import com.fis.lms_service.api.dto.response.submission.LessonSubmissionResponse;
import com.fis.lms_service.api.dto.response.submission.SubmissionAttachmentResponse;
import com.fis.lms_service.core.service.submission.LessonSubmissionService;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lesson-enrollments")
/** API nộp bài cho từng lesson enrollment. */
public class LessonSubmissionController {

    LessonSubmissionService lessonSubmissionService;

    @PostMapping(
            value = "/{lessonEnrollmentId}/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    /** Nộp/cập nhật bài nộp: thay toàn bộ file cũ bằng danh sách file mới. */
    public ResponseApi<LessonSubmissionResponse> submitLesson(
            @PathVariable("lessonEnrollmentId") String lessonEnrollmentId,
            @RequestPart("data") @Valid LessonSubmissionRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        var result =
                lessonSubmissionService.submitLesson(
                        parseId(lessonEnrollmentId, "lessonEnrollmentId"),
                        parseId(request.userId(), "userId"),
                        request.comment(),
                        files);

        var attachments =
                result.attachments().stream()
                        .map(
                                item ->
                                        new SubmissionAttachmentResponse(
                                                item.getFileName(), item.getFileUrl(), item.getFileSize()))
                        .toList();

        LessonSubmissionResponse response =
                new LessonSubmissionResponse(
                        result.lessonSubmissionId() == null ? null : result.lessonSubmissionId().toString(),
                        result.lessonEnrollmentId() == null ? null : result.lessonEnrollmentId().toString(),
                        result.submissionStatus().name(),
                        result.lastSubmissionAt(),
                        result.comment(),
                        attachments);

        return ResponseApi.ok(response);
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
}
