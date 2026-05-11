package com.intern.hub.api.dto.response.lesson;

import java.util.List;

public record LessonUserDetailResponse(
    String lessonId,
    String name,
    String introduction,
    String content,
    String requirements,
    String lessonImageUrl,
    List<LessonFileInfoResponse> files,
    String lessonEnrollmentId,
    String courseEnrollmentId,
    String lessonProgress) {}
