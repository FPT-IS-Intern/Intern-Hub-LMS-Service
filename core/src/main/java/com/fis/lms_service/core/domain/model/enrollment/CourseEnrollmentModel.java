package com.fis.lms_service.core.domain.model.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.constant.CourseProgress;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseEnrollmentModel {

  Long courseEnrollmentId;
  Long courseId;
  Long userId;
  CourseProgress courseProgress;
}
