package com.intern.hub.api.dto.response.submission;

public record SubmissionAttachmentResponse(
        String submissionAttachmentId, String fileName, String fileUrl, Long fileSize) {
}
