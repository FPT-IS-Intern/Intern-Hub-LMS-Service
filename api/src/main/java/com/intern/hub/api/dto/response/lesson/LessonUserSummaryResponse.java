package com.intern.hub.api.dto.response.lesson;

public record LessonUserSummaryResponse(
    String lessonId,
    String name,
    String lessonImageUrl,
    String lessonEnrollmentId,
    String courseEnrollmentId,
    String lessonProgress,
    Long createdAt,
    Long updatedAt) {}
