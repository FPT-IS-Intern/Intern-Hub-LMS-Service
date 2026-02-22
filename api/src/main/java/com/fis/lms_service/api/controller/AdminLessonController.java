package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.LessonFileService;
import com.fis.lms_service.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 1/29/2026
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/admin/lessons")
public class AdminLessonController {

    // Service
    LessonService lessonService;
    LessonFileService lessonFileService;

    // Mapper
    LessonApiMapper lessonApiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> createLesson(
            @RequestPart("data") @Valid LessonCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "assignmentFiles", required = false)
            List<MultipartFile> assignmentFiles) {

        LessonModel model = lessonApiMapper.toModel(request);
        lessonService.createLesson(model, image, lessonFiles, assignmentFiles);

        return ResponseApi.ok(true);
    }

    @GetMapping
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
            @PageableDefault(size = 10) Pageable pageable) {

        var lessonPage = lessonService.getLessons(pageable);

        var items = lessonPage.getContent().stream().map(lessonApiMapper::toSummaryResponse).toList();

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
            @PathVariable("lessonId") Long lessonId) {
        LessonModel model = lessonService.getLesson(lessonId);
        var fileModels = lessonFileService.getFiles(lessonId);

        List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
        LessonDetailResponse res = lessonApiMapper.toDetailResponse(model, files);

        return ResponseApi.ok(res);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> updateLesson(
            @PathVariable("lessonId") Long lessonId,
            @RequestPart("data") @Valid LessonCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "assignmentFiles", required = false) List<MultipartFile> assignmentFiles,
            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds) {

        LessonModel updateModel = lessonApiMapper.toModel(request);

        lessonService.updateLesson(
                lessonId, updateModel, image, lessonFiles, assignmentFiles, deleteFileIds);

        return ResponseApi.ok(true);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseApi<Boolean> deleteLesson(@PathVariable("lessonId") Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseApi.ok(true);
    }
}
