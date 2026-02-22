package com.fis.lms_service.core.repository.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.LessonEnrollmentModel;

import java.util.List;

public interface LessonEnrollmentRepository {

  List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);

  void saveAll(List<LessonEnrollmentModel> models);
}
