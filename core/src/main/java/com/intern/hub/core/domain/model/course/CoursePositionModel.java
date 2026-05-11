package com.intern.hub.core.domain.model.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoursePositionModel {

  Long coursePositionId;
  Long courseId;
  Long positionId;
}
