package com.intern.hub.core.repository.course;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseEvaluatorRepository {

  Page<EvaluatorCourseOverviewModel> findAllCourseOverviews(
      Long evaluatorUserId, Pageable pageable);

  Page<EvaluatorCourseOverviewModel> findCourseOverviewsByEvaluatorUserId(
      Long evaluatorUserId, Pageable pageable);
}
