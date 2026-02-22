package com.fis.lms_service.api.controller;

import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.api.mapper.LessonApiMapper;
import com.fis.lms_service.core.service.lesson.LessonService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/courses")
public class CourseLessonQueryController {

    LessonService lessonService;
    LessonApiMapper lessonApiMapper;

    @GetMapping("/{courseId}/lessons")
    public ResponseApi<PaginatedData<LessonSummaryResponse>> getCourseLessons(
            @PathVariable("courseId") String courseId,
            @RequestParam(value = "userId", required = false) String userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Long courseIdValue = parseId(courseId, "courseId");
        Long userIdValue = parseOptionalId(userId, "userId");

        var page = lessonService.getLessonsByCourse(courseIdValue, pageable);
        var items =
                page.getContent().stream()
                        .map(
                                model ->
                                        lessonApiMapper.toSummaryResponse(
                                                model,
                                                lessonService.getLessonEnrollmentId(
                                                        model.getLessonId(), userIdValue)))
                        .toList();

        var res =
                PaginatedData.<LessonSummaryResponse>builder()
                        .items(items)
                        .totalItems(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build();

        return ResponseApi.ok(res);
    }

    private Long parseId(String value, String field) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new com.intern.hub.library.common.exception.BadRequestException(
                    "id.invalid", field + " không hợp lệ");
        }
    }

    private Long parseOptionalId(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseId(value, field);
    }
}
