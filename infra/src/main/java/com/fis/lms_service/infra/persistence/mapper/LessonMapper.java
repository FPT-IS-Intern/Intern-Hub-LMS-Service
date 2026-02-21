package com.fis.lms_service.infra.persistence.mapper;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/** Admin 1/29/2026 */
@Mapper(componentModel = "spring")
public interface LessonMapper {
  LessonEntity toEntity(LessonModel model);

  LessonModel toModel(LessonEntity entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromModel(LessonModel model, @MappingTarget LessonEntity entity);
}
