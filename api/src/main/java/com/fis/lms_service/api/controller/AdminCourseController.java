package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.request.CourseCreateRequest;
import com.fis.lms_service.api.dto.response.course.CourseDetailResponse;
import com.fis.lms_service.api.dto.response.course.CourseSummaryResponse;
import com.fis.lms_service.api.mapper.CourseApiMapper;
import com.fis.lms_service.core.service.course.CourseService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/courses")
public class AdminCourseController {

  CourseService courseService;
  CourseApiMapper courseApiMapper;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseApi<Boolean> createCourse(
      @RequestPart("data") @Valid CourseCreateRequest request,
      @RequestPart(value = "image", required = true) MultipartFile image) {

    courseService.createCourse(courseApiMapper.toModel(request), image, request.lessonIds());
    return ResponseApi.ok(true);
  }

  @GetMapping
  public ResponseApi<PaginatedData<CourseSummaryResponse>> getCourses(
      @PageableDefault(size = 10) Pageable pageable) {
    var page = courseService.getCourses(pageable);
    var items = page.getContent().stream().map(courseApiMapper::toSummaryResponse).toList();

    var res =
        PaginatedData.<CourseSummaryResponse>builder()
            .items(items)
            .totalItems(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    return ResponseApi.ok(res);
  }

  @GetMapping("/{courseId}")
  public ResponseApi<CourseDetailResponse> getCourse(@PathVariable("courseId") Long courseId) {
    return ResponseApi.ok(courseApiMapper.toDetailResponse(courseService.getCourse(courseId)));
  }

  @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseApi<Boolean> updateCourse(
      @PathVariable("courseId") Long courseId,
      @RequestPart("data") @Valid CourseCreateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    courseService.updateCourse(courseId, courseApiMapper.toModel(request), image);
    return ResponseApi.ok(true);
  }

  @DeleteMapping("/{courseId}")
  public ResponseApi<Boolean> deleteCourse(@PathVariable("courseId") Long courseId) {
    courseService.deleteCourse(courseId);
    return ResponseApi.ok(true);
  }
}
