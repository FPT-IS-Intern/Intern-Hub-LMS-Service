package com.intern.hub.api.dto.response.course;

import java.util.List;

public record CourseDetailResponse(
        String courseId,
        String name,
        String description,
        String courseImageUrl,
        List<String> lessonIds) {
}
