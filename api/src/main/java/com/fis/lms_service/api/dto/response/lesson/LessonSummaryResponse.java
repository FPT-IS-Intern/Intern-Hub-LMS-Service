package com.fis.lms_service.api.dto.response.lesson;

import lombok.Builder;

/** Admin 2/5/2026 \ */
@Builder
public record LessonSummaryResponse(
    String lessonId,
    String name,
    String lessonImageUrl,
    String lessonEnrollmentId,
    Long createdAt,
    Long updatedAt) {}
