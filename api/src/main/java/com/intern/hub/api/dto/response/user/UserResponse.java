package com.intern.hub.api.dto.response.user;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for user data. Mapping from UserModel is handled by MapStruct in UserApiMapper.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long userId;

    /**
     * Thông tin chung
     */
    String email;

    String fullName;
    String idNumber;
    String phoneNumber;
    String address;
    LocalDate dateOfBirth;
    String avatarUrl;

    /**
     * Công việc
     */
    String positionCode;

    String role;
    String cvUrl;
    String superVisorName;
    LocalDate internshipStartDate;
    LocalDate internshipEndDate;

    /**
     * Trạng thái người dùng
     */
    String status;
}
