package com.intern.hub.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CourseEnrollRequest(@NotBlank(message = "userId là bắt buộc") String userId) {
}
