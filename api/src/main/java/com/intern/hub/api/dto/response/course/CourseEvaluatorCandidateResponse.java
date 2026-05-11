package com.intern.hub.api.dto.response.course;

public record CourseEvaluatorCandidateResponse(
    String userId, String email, String fullName, String role, String avatarUrl) {}
