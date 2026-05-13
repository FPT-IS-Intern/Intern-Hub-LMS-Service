package com.intern.hub.core.domain.model.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluatorCourseOverviewModel {

  Long courseId;
  String name;
  String courseImageUrl;
  Long totalEnrollmentCount;
  Long completedEnrollmentCount;
  Long notCompletedEnrollmentCount;
  boolean canEvaluate;
}
