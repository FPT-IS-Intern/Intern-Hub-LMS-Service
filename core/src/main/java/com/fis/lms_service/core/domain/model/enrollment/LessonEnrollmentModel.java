package com.fis.lms_service.core.domain.model.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.constant.LessonProgress;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonEnrollmentModel {

  Long lessonEnrollmentId;
  Long courseEnrollmentId;
  Long lessonId;
  LessonProgress lessonProgress;
}
