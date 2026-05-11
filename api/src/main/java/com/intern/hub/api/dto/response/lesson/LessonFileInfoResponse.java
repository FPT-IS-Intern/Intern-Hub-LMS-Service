package com.intern.hub.api.dto.response.lesson;

import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import lombok.Builder;

/** Admin 2/11/2026 */
@Builder
public record LessonFileInfoResponse(
    String lessonFileId,
    String fileUrl,
    String fileName,
    LessonFileType lessonFileType,
    Long fileSize) {}
