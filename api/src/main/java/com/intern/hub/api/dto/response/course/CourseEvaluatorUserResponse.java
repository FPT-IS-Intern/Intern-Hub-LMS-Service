package com.intern.hub.api.dto.response.course;

public record CourseEvaluatorUserResponse(
        String userId,
        String email,
        String fullName,
        String role,
        String avatarUrl) {
}

