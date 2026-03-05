package com.intern.hub.api.dto.response.course;

import lombok.Builder;

@Builder
public record CourseSummaryResponse(
    String courseId, String name, String courseImageUrl, Long createdAt, Long updatedAt) {}
