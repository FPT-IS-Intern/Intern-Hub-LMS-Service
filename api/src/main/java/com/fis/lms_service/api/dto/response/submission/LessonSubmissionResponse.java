package com.fis.lms_service.api.dto.response.submission;

import java.util.List;

public record LessonSubmissionResponse(
    String lessonSubmissionId,
    String lessonEnrollmentId,
    String submissionStatus,
    Long lastSubmissionAt,
    String comment,
    List<SubmissionAttachmentResponse> attachments) {}
