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
  @Mapping(target = "lessonId", ignore = true)
  @Mapping(target = "lessonImageUrl", ignore = true)
  LessonModel toModel(LessonCreateRequest request);

  @Mapping(target = "lessonEnrollmentId", source = "lessonEnrollmentId")
  LessonSummaryResponse toSummaryResponse(LessonModel model, Long lessonEnrollmentId);

  @Mapping(target = "files", source = "files")
  @Mapping(target = "lessonEnrollmentId", source = "lessonEnrollmentId")
  LessonDetailResponse toDetailResponse(
      LessonModel model, List<LessonFileInfoResponse> files, Long lessonEnrollmentId);

  LessonFileInfoResponse toFileResponse(LessonFileModel fileModel);

  List<LessonFileInfoResponse> toFileResponseList(List<LessonFileModel> fileModels);

  default String map(Long value) {
    return value == null ? null : value.toString();
  }
}
