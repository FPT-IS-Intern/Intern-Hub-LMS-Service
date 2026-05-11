package com.intern.hub.api.dto.response.reference;

public record AdminUserReferenceResponse(
    String userId, String email, String fullName, String role, String avatarUrl) {}
