package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonRequestMapper;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 1/29/2026
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons")
public class LessonController {

    LessonService lessonService;
    LessonRequestMapper lessonRequestMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> createLesson(
            @RequestPart("data") @Valid LessonCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "assignmentFiles", required = false) List<MultipartFile> assignmentFiles) {

        LessonModel model = lessonRequestMapper.toModel(request);
        lessonService.createLesson(model, image, lessonFiles, assignmentFiles);

        return ResponseApi.ok(true);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseApi<Boolean> deleteLesson(@PathVariable("lessonId") Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseApi.ok(true);
    }

    @GetMapping
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
            @PageableDefault(size = 10) Pageable pageable) {

        var lessonPage = lessonService.findAll(pageable);

        var items = lessonPage
                .getContent()
                .stream()
                .map(lessonRequestMapper::toDto)
                .toList();

        var res = PaginatedData
                .<LessonSummaryResponse>builder()
                .items(items)
                .totalItems(lessonPage.getTotalElements())
                .totalPages(lessonPage.getTotalPages())
                .build();

        return ResponseApi.ok(res);
    }

}
