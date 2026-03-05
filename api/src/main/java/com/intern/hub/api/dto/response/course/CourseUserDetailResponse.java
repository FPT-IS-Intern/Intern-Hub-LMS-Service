package com.intern.hub.api.dto.response.course;

import com.intern.hub.api.dto.response.lesson.LessonUserSummaryResponse;
import java.util.List;

public record CourseUserDetailResponse(
    String courseId,
    String name,
    String description,
    String courseImageUrl,
    boolean enrolled,
    String courseEnrollmentId,
    String courseProgress,
    List<LessonUserSummaryResponse> lessons) {}
