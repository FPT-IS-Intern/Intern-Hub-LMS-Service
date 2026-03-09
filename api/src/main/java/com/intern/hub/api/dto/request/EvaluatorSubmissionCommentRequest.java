package com.intern.hub.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EvaluatorSubmissionCommentRequest(@NotBlank(message = "comment khong duoc de trong") String comment) {}
