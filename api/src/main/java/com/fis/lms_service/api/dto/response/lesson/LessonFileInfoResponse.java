package com.fis.lms_service.api.dto.response.lesson;

import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import lombok.Builder;

/** Admin 2/11/2026 */
@Builder
public record LessonFileInfoResponse(
    String lessonFileId,
    String fileUrl,
    String fileName,
    LessonFileType lessonFileType,
    Long fileSize) {}
