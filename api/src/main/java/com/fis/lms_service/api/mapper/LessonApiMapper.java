package com.fis.lms_service.api.mapper;

import com.fis.lms_service.api.dto.request.LessonCreateRequest;
import com.fis.lms_service.api.dto.response.lesson.LessonDetailResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.api.dto.response.lesson.LessonSummaryResponse;
import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Admin 1/29/2026 */
@Mapper(componentModel = "spring")
public interface LessonApiMapper {
  LessonModel toModel(LessonCreateRequest request);

  LessonSummaryResponse toSummaryResponse(LessonModel model);

  @Mapping(target = "files", source = "files")
  LessonDetailResponse toDetailResponse(LessonModel model, List<LessonFileInfoResponse> files);

  LessonFileInfoResponse toFileResponse(LessonFileModel fileModel);

  List<LessonFileInfoResponse> toFileResponseList(List<LessonFileModel> fileModels);

  default String map(Long value) {
    return value == null ? null : value.toString();
  }
}
