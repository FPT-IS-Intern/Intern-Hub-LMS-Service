package com.fis.lms_service.api.mapper;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Admin 1/29/2026
 */
@Mapper(componentModel = "spring")
public interface LessonRequestMapper {
    LessonModel toModel(LessonCreateRequest request);

    LessonSummaryResponse toDto(LessonModel model);

    @Mapping(target = "files", ignore = true)
    LessonDetailResponse toDetailDto(LessonModel model);

}
