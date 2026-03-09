package com.intern.hub.api.dto.response.submission;

import java.util.List;

public record EvaluatorSubmissionOverviewResponse(
        String lessonSubmissionId,
        String courseEnrollmentId,
        String lessonEnrollmentId,
        String lessonId,
        String lessonName,
        String userId,
        String userEmail,
        String userFullName,
        String userAvatarUrl,
        String submissionStatus,
        Long lastSubmissionAt,
        String comment,
        List<SubmissionAttachmentResponse> attachments) {
}
