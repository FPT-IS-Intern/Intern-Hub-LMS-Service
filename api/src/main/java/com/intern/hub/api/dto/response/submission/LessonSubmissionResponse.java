package com.intern.hub.api.dto.response.submission;

import java.util.List;

public record LessonSubmissionResponse(
        String lessonSubmissionId,
        String lessonEnrollmentId,
        String submissionStatus,
        String evaluationStatus,
        Long lastSubmissionAt,
        String comment,
        List<SubmissionAttachmentResponse> attachments) {
}
