package com.fis.lms_service.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CourseEnrollRequest(@NotNull(message = "userId là bắt buộc") Long userId) {}
