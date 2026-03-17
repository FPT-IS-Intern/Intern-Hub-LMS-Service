package com.intern.hub.api.controller;

import com.intern.hub.api.dto.request.LessonCreateRequest;
import com.intern.hub.api.dto.response.lesson.LessonDetailResponse;
import com.intern.hub.api.dto.response.lesson.LessonFileInfoResponse;
import com.intern.hub.api.dto.response.lesson.LessonSummaryResponse;
import com.intern.hub.api.mapper.LessonApiMapper;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.service.lesson.AdminLessonService;
import com.intern.hub.core.service.lesson.LessonFileService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Admin 1/29/2026 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/admin/lessons")
@Tag(
    name = "Admin Lesson",
    description = "Quản trị bài học: CRUD và quản lý file tài liệu/bài tập.")
public class AdminLessonController {

  // Service
  AdminLessonService adminLessonService;
  LessonFileService lessonFileService;

  // Mapper
  LessonApiMapper lessonApiMapper;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Authenticated
  @HasPermission(resource = "quan-ly-bai-hoc", action = Action.CREATE)
  @Operation(
      summary = "Tạo bài học",
      description = "Tạo bài học mới (multipart: data + image + file đính kèm).")
  public ResponseApi<?> createLesson(
      @RequestPart("data") @Valid LessonCreateRequest request,
      @RequestPart(value = "image", required = true) MultipartFile image,
      @RequestPart(value = "lessonFiles", required = false) List<MultipartFile> lessonFiles,
      @RequestPart(value = "assignmentFiles", required = false)
          List<MultipartFile> assignmentFiles) {

    LessonModel model = lessonApiMapper.toModel(request);
    adminLessonService.createLesson(
        model, image, lessonFiles, assignmentFiles, UserContext.requiredUserId());

    return ResponseApi.noContent();
  }

  @GetMapping
  @Authenticated
  @HasPermission(resource = "quan-ly-bai-hoc", action = Action.READ)
  @Operation(summary = "Danh sách bài học", description = "Lấy danh sách bài học có phân trang.")
  public ResponseApi<PaginatedData<LessonSummaryResponse>> getLessons(
      @PageableDefault(size = 10) Pageable pageable) {

    var lessonPage = adminLessonService.getLessons(pageable);
    var res = PaginationUtils.toPaginatedData(lessonPage, lessonApiMapper::toSummaryResponse);

    return ResponseApi.ok(res);
  }

  @GetMapping("/{lessonId}")
  @Authenticated
  @HasPermission(resource = "quan-ly-bai-hoc", action = Action.READ)
  @Operation(summary = "Chi tiết bài học", description = "Lấy chi tiết bài học kèm danh sách file.")
  public ResponseApi<LessonDetailResponse> getLessonDetail(
      @PathVariable("lessonId") String lessonId) {
    Long lessonIdValue = parseId(lessonId, "lessonId");
    LessonModel model = adminLessonService.getLesson(lessonIdValue);
    var fileModels = lessonFileService.getFiles(lessonIdValue);

    List<LessonFileInfoResponse> files = lessonApiMapper.toFileResponseList(fileModels);
    LessonDetailResponse res = lessonApiMapper.toDetailResponse(model, files);

    return ResponseApi.ok(res);
  }

  @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Authenticated
  @HasPermission(resource = "quan-ly-bai-hoc", action = Action.UPDATE)
  @Operation(
      summary = "Cập nhật bài học",
      description = "Cập nhật bài học, hỗ trợ thêm/xóa file và thay ảnh.")
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
        parseIds(deleteFileIds, "deleteFileIds"),
        UserContext.requiredUserId());

    return ResponseApi.noContent();
  }

  @DeleteMapping("/{lessonId}")
  @Authenticated
  @HasPermission(resource = "quan-ly-bai-hoc", action = Action.DELETE)
  @Operation(summary = "Xóa bài học", description = "Xóa bài học theo id.")
  public ResponseApi<?> deleteLesson(@PathVariable("lessonId") String lessonId) {
    adminLessonService.deleteLesson(parseId(lessonId, "lessonId"), UserContext.requiredUserId());
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
