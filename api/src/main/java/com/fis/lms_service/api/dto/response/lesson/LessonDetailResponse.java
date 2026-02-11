package com.fis.lms_service.api.dto.response.lesson;

/**
 * Admin 2/11/2026
 *
 **/
public record LessonDetailResponse(
        Long lessonId,
        String name,
        String introduction,
        String content,
        String requirements,
        String lessonImageUrl
) {
}
