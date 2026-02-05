package com.fis.lms_service.api.dto.response;

import lombok.Builder;

/**
 * Admin 2/5/2026
 * \
 **/
@Builder
public record LessonSummaryResponse(
        Long id,
        String title,
        String imageUrl
) {
}
