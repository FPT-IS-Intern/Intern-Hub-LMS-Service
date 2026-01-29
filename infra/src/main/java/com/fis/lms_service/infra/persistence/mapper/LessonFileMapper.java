package com.fis.lms_service.infra.persistence.mapper;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Admin 1/29/2026 */
@Mapper(componentModel = "spring")
public interface LessonFileMapper {

  @Mapping(target = "lessonEntity.lessonId", source = "lessonId")
  LessonFileEntity toEntity(LessonFileModel model);

  @Mapping(target = "lessonId", source = "lessonEntity.lessonId")
  LessonFileModel toModel(LessonFileEntity entity);
}
