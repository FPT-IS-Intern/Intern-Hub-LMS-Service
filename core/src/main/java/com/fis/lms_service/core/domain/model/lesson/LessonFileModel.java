package com.fis.lms_service.core.domain.model.lesson;

import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonFileModel {

  Long lessonFileId;
  Long lessonId;
  String fileUrl;
  String fileName;
  LessonFileType lessonFileType;
  Long fileSize;
}
