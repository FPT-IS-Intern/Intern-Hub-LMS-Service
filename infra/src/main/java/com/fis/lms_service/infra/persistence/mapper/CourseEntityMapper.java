package com.fis.lms_service.infra.persistence.mapper;

import com.fis.lms_service.core.domain.model.course.CourseModel;
import com.fis.lms_service.infra.persistence.entity.course.CourseEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CourseEntityMapper {

  CourseEntity toEntity(CourseModel model);

  CourseModel toModel(CourseEntity entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromModel(CourseModel model, @MappingTarget CourseEntity entity);
}
