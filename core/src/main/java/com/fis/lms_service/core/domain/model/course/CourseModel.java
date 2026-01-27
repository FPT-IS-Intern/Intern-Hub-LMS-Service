package com.fis.lms_service.core.domain.model.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModel {

  Long courseId;
  String name;
  String description;
  String courseImageUrl;
}
