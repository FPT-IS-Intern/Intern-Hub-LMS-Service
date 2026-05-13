package com.intern.hub.core.domain.model.enrollment;

import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Data
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
