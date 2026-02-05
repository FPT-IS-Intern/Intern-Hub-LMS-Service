package com.fis.lms_service.api.dto.response;

import lombok.Builder;

/**
 * Admin 2/5/2026
 * \
 **/
@Builder
public record LessonSummaryResponse(
        Long lessonId,
        String name,
        String lessonImageUrl
) {
}
