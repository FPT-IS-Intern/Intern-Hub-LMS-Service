package com.intern.hub.api.dto.response.evaluator;

public record EvaluatorCourseOverviewResponse(
        String courseId,
        String name,
        String courseImageUrl,
        Long totalEnrollmentCount,
        Long completedEnrollmentCount,
        Long notCompletedEnrollmentCount,
        boolean canEvaluate) {
}
