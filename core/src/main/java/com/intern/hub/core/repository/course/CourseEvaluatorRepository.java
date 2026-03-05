package com.intern.hub.core.repository.course;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import java.util.List;

public interface CourseEvaluatorRepository {

    List<EvaluatorCourseOverviewModel> findAllCourseOverviews();

    List<EvaluatorCourseOverviewModel> findCourseOverviewsByEvaluatorUserId(Long evaluatorUserId);
}
