package com.fis.lms_service.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
    @NotBlank(message = "Tên khóa học không được để trống")
        @Size(max = 128, message = "Tên khóa học tối đa 128 ký tự")
        String name,
    @NotBlank(message = "Mô tả không được để trống")
        @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
        String description) {}
