package com.intern.hub.core.repository.course;

import java.util.List;

public interface CourseEvaluatorAssignmentRepository {

  void saveCourseEvaluators(Long courseId, List<Long> evaluatorUserIds);

  void replaceCourseEvaluators(Long courseId, List<Long> evaluatorUserIds);

  List<Long> findEvaluatorUserIdsByCourseId(Long courseId);
}
