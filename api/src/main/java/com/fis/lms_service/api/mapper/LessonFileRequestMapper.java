package com.fis.lms_service.api.mapper;

import com.fis.lms_service.api.dto.response.lesson.LessonFileInfoResponse;
import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Admin 2/11/2026 */
@Mapper(componentModel = "spring")
public interface LessonFileRequestMapper {

  @Mapping(target = "lessonFileId", source = "lessonFileId")
  LessonFileInfoResponse toFileDto(LessonFileModel fileModel);

  List<LessonFileInfoResponse> toFileDtoList(List<LessonFileModel> fileModels);
}
