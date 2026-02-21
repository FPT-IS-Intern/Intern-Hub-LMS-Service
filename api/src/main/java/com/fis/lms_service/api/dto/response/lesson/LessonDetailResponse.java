package com.fis.lms_service.api.dto.response.lesson;

import java.util.List;

/** Admin 2/11/2026 */
public record LessonDetailResponse(
    String lessonId,
    String name,
    String introduction,
    String content,
    String requirements,
    String lessonImageUrl,
    List<LessonFileInfoResponse> files) {}
