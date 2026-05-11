package com.intern.hub.infra.feign.model;

import java.time.LocalDate;

public record HrmUserClientModel(
    Long userId,
    String email,
    String fullName,
    String idNumber,
    String phoneNumber,
    String address,
    LocalDate dateOfBirth,
    String avatarUrl,
    String positionCode,
    String role,
    String cvUrl,
    String superVisorName,
    LocalDate internshipStartDate,
    LocalDate internshipEndDate,
    String sysStatus) {}
