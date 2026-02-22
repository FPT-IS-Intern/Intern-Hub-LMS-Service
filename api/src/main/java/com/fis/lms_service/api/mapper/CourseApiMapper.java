package com.fis.lms_service.api.mapper;

import com.fis.lms_service.api.dto.request.CourseCreateRequest;
import com.fis.lms_service.api.dto.response.course.CourseDetailResponse;
import com.fis.lms_service.api.dto.response.course.CourseSummaryResponse;
import com.fis.lms_service.core.domain.model.course.CourseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseApiMapper {

  @Mapping(target = "courseId", ignore = true)
  @Mapping(target = "courseImageUrl", ignore = true)
  CourseModel toModel(CourseCreateRequest request);

  CourseSummaryResponse toSummaryResponse(CourseModel model);

  CourseDetailResponse toDetailResponse(CourseModel model);

  default String map(Long value) {
    return value == null ? null : value.toString();
  }
}
