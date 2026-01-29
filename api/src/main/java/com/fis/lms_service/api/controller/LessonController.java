package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.mapper.LessonRequestMapper;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.LessonService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Admin 1/29/2026 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons")
public class LessonController {

  LessonService lessonService;
  LessonRequestMapper lessonRequestMapper;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<@NonNull Void> createLesson(
      @RequestPart("data") @Valid LessonCreateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
      @RequestPart(value = "assignmentFiles", required = false)
          List<MultipartFile> assignmentFiles) {
    LessonModel model = lessonRequestMapper.toModel(request);

    lessonService.createLesson(model, image, lessonFiles, assignmentFiles);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
