package com.intern.hub.api.mapper;

import com.intern.hub.api.dto.request.CourseCreateRequest;
import com.intern.hub.api.dto.response.course.CourseSummaryResponse;
import com.intern.hub.core.domain.model.course.CourseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseApiMapper {

    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "courseImageUrl", ignore = true)
    CourseModel toModel(CourseCreateRequest request);

    CourseSummaryResponse toSummaryResponse(CourseModel model);

    default String map(Long value) {
        return value == null ? null : value.toString();
    }
}
