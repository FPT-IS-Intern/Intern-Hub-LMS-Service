package com.intern.hub.api.dto.response.course;

import com.intern.hub.api.dto.response.lesson.LessonSummaryResponse;
import java.util.List;

public record CourseDetailResponse(
    String courseId,
    String name,
    String description,
    String courseImageUrl,
    List<LessonSummaryResponse> lessons) {}
