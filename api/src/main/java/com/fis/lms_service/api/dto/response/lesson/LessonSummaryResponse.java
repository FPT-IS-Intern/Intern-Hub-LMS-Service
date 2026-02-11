package com.fis.lms_service.api.dto.response.lesson;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;

/**
 * Admin 2/5/2026
 * \
 **/
@Builder
public record LessonSummaryResponse(
        String lessonId,
        String name,
        String lessonImageUrl
) {
}
