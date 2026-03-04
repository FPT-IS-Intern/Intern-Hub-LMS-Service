package com.intern.hub.api.dto.response.course;

public record CourseUserSummaryResponse(
        String courseId,
        String name,
        String courseImageUrl,
        Long createdAt,
        Long updatedAt,
        boolean enrolled,
        String courseEnrollmentId,
        String courseProgress) {
}
