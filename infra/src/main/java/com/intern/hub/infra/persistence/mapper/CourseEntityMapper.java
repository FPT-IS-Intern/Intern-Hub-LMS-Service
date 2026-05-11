package com.intern.hub.infra.persistence.mapper;

import com.intern.hub.core.domain.model.course.CourseModel;
import com.intern.hub.infra.persistence.entity.course.CourseEntity;
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
