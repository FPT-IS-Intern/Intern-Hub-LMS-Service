package com.intern.hub.core.domain.model.lesson;

import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Data
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
