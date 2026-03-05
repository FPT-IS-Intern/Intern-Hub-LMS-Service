package com.intern.hub.core.domain.model.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
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
}

