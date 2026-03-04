package com.intern.hub.api.dto.response.enrollment;

public record LessonEnrollmentResponse(
        String lessonEnrollmentId,
        String courseEnrollmentId,
        String lessonId,
        String lessonProgress) {
}
