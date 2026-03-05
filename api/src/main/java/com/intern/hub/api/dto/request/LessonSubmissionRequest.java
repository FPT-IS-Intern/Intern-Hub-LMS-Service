package com.intern.hub.api.dto.request;

import java.util.List;

public record LessonSubmissionRequest(String comment, List<String> deleteAttachmentIds) {}
