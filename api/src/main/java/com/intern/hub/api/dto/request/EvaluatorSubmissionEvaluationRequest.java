package com.intern.hub.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EvaluatorSubmissionEvaluationRequest(
        @NotBlank(message = "evaluationStatus khong duoc de trong") String evaluationStatus) {}
