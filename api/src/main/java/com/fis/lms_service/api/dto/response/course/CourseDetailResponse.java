package com.fis.lms_service.api.dto.response.course;

public record CourseDetailResponse(
    String courseId, String name, String description, String courseImageUrl) {}
