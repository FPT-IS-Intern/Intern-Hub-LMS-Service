package com.intern.hub.api.dto.response.lesson;

import lombok.Builder;

/**
 * Admin 2/5/2026 \
 */
@Builder
public record LessonSummaryResponse(
        String lessonId,
        String name,
        String lessonImageUrl,
        Long createdAt,
        Long updatedAt) {
}
