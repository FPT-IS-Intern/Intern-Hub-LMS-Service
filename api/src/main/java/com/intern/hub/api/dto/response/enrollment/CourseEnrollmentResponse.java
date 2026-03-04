package com.intern.hub.api.dto.response.enrollment;

public record CourseEnrollmentResponse(
        String courseEnrollmentId,
        String courseId,
        String userId,
        String courseProgress) {
}
