package com.fis.lms_service.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Admin 1/29/2026
 *
 **/
public record LessonCreateRequest(
        @NotBlank(message = "Tên bài học không được để trống")
        @Size(max = 255, message = "Tên bài học tối đa 255 ký tự")
        String name,

        @NotBlank(message = "Giới thiệu không được để trống")
        @Size(max = 255, message = "Giới thiệu tối đa 255 ký tự")
        String introduction,

        @NotBlank(message = "Nội dung không được để trống")
        @Size(max = 255, message = "Nội dung tối đa 255 ký tự")
        String content,

        @NotBlank(message = "Tiêu chí hoàn thành không được để trống")
        @Size(max = 255, message = "Tiêu chí tối đa 255 ký tự")
        String requirements
) {
}