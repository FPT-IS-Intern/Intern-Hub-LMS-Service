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
        String evaluationStatus,
        Long lastSubmissionAt,
        String learnerNote,
        String evaluatorComment,
        List<SubmissionAttachmentResponse> attachments) {
}
