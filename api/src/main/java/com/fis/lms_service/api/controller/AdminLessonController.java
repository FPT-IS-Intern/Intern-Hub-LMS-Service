package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.api.util.PaginationUtils;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.service.lesson.AdminLessonService;
import com.fis.lms_service.core.service.lesson.LessonFileService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
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
@RequestMapping("/admin/lessons")
/** API quản trị bài học: CRUD bài học và quản lý file tài liệu/bài tập. */
public class AdminLessonController {

    // Service
    AdminLessonService adminLessonService;
    LessonFileService lessonFileService;

    // Mapper
    LessonApiMapper lessonApiMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    /** Tạo bài học mới (multipart: data + image + file đính kèm). */
    public ResponseApi<?> createLesson(
            @RequestPart("data") @Valid LessonCreateRequest request,
            @RequestPart(value = "image", required = true) MultipartFile image,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "assignmentFiles", required = false)
            List<MultipartFile> assignmentFiles) {

        LessonModel model = lessonApiMapper.toModel(request);
        adminLessonService.createLesson(model, image, lessonFiles, assignmentFiles);

        return ResponseApi.noContent();
    }

    @GetMapping
    /** Lấy danh sách bài học có phân trang. */
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
            @PageableDefault(size = 10) Pageable pageable) {

        var lessonPage = adminLessonService.getLessons(pageable);
        var res =
                PaginationUtils.toPaginatedData(
                        lessonPage, model -> lessonApiMapper.toSummaryResponse(model, null));

        return ResponseApi.ok(res);
    }

    @GetMapping("/{lessonId}")
    /** Lấy chi tiết bài học kèm danh sách file. */
    public ResponseApi<LessonDetailResponse> getLessonDetail(
            @PathVariable("lessonId") String lessonId) {
        Long lessonIdValue = parseId(lessonId, "lessonId");
        LessonModel model = adminLessonService.getLesson(lessonIdValue);
        var fileModels = lessonFileService.getFiles(lessonIdValue);

        List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
        LessonDetailResponse res = lessonApiMapper.toDetailResponse(model, files, null);

        return ResponseApi.ok(res);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    /** Cập nhật bài học, hỗ trợ thêm/xóa file và thay ảnh. */
    public ResponseApi<?> updateLesson(
            @PathVariable("lessonId") String lessonId,
            @RequestPart("data") @Valid LessonCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
            @RequestPart(value = "assignmentFiles", required = false) List<MultipartFile> assignmentFiles,
            @RequestParam(value = "deleteFileIds", required = false) List<String> deleteFileIds) {

        LessonModel updateModel = lessonApiMapper.toModel(request);

        adminLessonService.updateLesson(
                parseId(lessonId, "lessonId"),
                updateModel,
                image,
                lessonFiles,
                assignmentFiles,
                parseIds(deleteFileIds, "deleteFileIds"));

        return ResponseApi.noContent();
    }

    @DeleteMapping("/{lessonId}")
    /** Xóa bài học theo id. */
    public ResponseApi<?> deleteLesson(@PathVariable("lessonId") String lessonId) {
        adminLessonService.deleteLesson(parseId(lessonId, "lessonId"));
        return ResponseApi.noContent();
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
