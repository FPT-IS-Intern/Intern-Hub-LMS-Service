package com.fis.lms_service.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record LessonSubmissionRequest(
    @NotNull(message = "userId là bắt buộc") Long userId,
    String comment) {}
