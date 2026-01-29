package com.fis.lms_service.infra.persistence.mapper;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import org.mapstruct.Mapper;

/**
 * Admin 1/29/2026
 */
@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonEntity toEntity(LessonModel model);

    LessonModel toModel(LessonEntity entity);
}
