package com.fis.lms_service.core.repository.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.LessonEnrollmentModel;

import java.util.List;
import java.util.Optional;

public interface LessonEnrollmentRepository {

  List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);

  Optional<Long> findLessonEnrollmentId(Long courseEnrollmentId, Long lessonId);

  Optional<Long> findUserIdByLessonEnrollmentId(Long lessonEnrollmentId);

  void saveAll(List<LessonEnrollmentModel> models);
}
