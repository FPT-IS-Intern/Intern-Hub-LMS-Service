package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonSubmissionRequest;
import com.fis.lms_service.core.service.submission.LessonSubmissionService;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lesson-enrollments")
public class LessonSubmissionController {

  LessonSubmissionService lessonSubmissionService;

  @PostMapping(value = "/{lessonEnrollmentId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseApi<Boolean> submitLesson(
      @PathVariable("lessonEnrollmentId") Long lessonEnrollmentId,
      @RequestPart("data") @Valid LessonSubmissionRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    lessonSubmissionService.submitLesson(
        lessonEnrollmentId, request.userId(), request.comment(), files);
    return ResponseApi.ok(true);
  }
}
