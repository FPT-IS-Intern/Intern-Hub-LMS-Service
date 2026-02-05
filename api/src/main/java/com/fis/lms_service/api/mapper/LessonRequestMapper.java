package com.fis.lms_service.api.mapper;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.LessonSummaryResponse;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import org.mapstruct.Mapper;

/**
 * Admin 1/29/2026
 */
@Mapper(componentModel = "spring")
public interface LessonRequestMapper {
    LessonModel toModel(LessonCreateRequest request);

    LessonSummaryResponse toDto(LessonModel model);

}
