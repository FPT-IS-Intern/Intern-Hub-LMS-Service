package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonFileRequestMapper;
import com.fis.lms_service.api.mapper.LessonRequestMapper;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.LessonFileService;
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

    // Service
    LessonService lessonService;
    LessonFileService lessonFileService;

    // Mapper
    LessonRequestMapper lessonRequestMapper;
    LessonFileRequestMapper lessonFileRequestMapper;

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

    @GetMapping
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
            @PageableDefault(size = 10) Pageable pageable) {

        var lessonPage = lessonService.getLessons(pageable);

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

    @GetMapping("/{lessonId}")
    public ResponseApi<LessonDetailResponse> getLessonDetail(@PathVariable("lessonId") String lessonId) {
        Long id = Long.parseLong(lessonId);

        LessonModel model = lessonService.getLesson(id);
        var fileModels = lessonFileService.getFiles(id);

        LessonDetailResponse detailWithoutFiles = lessonRequestMapper.toDetailDto(model);
        List<LessonFileInfoResponse> files = lessonFileRequestMapper.toFileDtoList(fileModels);

        LessonDetailResponse res = new LessonDetailResponse(
                detailWithoutFiles.lessonId(),
                detailWithoutFiles.name(),
                detailWithoutFiles.introduction(),
                detailWithoutFiles.content(),
                detailWithoutFiles.requirements(),
                detailWithoutFiles.lessonImageUrl(),
                files
        );

        return ResponseApi.ok(res);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseApi<Boolean> updateLesson(
            @PathVariable("lessonId") String lessonId,
            @RequestPart("updateModel") @Valid LessonCreateRequest request,
            @RequestPart(value = "newImage", required = false) MultipartFile image,
            @RequestPart(value = "newLessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "newAssignmentFiles", required = false) List<MultipartFile> assignmentFiles,
            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds) {

        LessonModel updateModel = lessonRequestMapper.toModel(request);
        
        lessonService.updateLesson(
                Long.parseLong(lessonId),
                updateModel,
                image,
                lessonFiles,
                assignmentFiles,
                deleteFileIds
        );

        return ResponseApi.ok(true);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseApi<Boolean> deleteLesson(@PathVariable("lessonId") String lessonId) {
        lessonService.deleteLesson(Long.parseLong(lessonId));
        return ResponseApi.ok(true);
    }

}
