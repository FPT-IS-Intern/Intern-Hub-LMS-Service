package com.fis.lms_service.core.repository.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.LessonEnrollmentModel;
import com.fis.lms_service.core.domain.model.enrollment.constant.LessonProgress;

import java.util.List;
import java.util.Optional;

public interface LessonEnrollmentRepository {

  List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);

  Optional<Long> findLessonEnrollmentId(Long courseEnrollmentId, Long lessonId);

  Optional<Long> findUserIdByLessonEnrollmentId(Long lessonEnrollmentId);

  Optional<Long> findCourseEnrollmentIdByLessonEnrollmentId(Long lessonEnrollmentId);

  long countByCourseEnrollmentId(Long courseEnrollmentId);

  long countByCourseEnrollmentIdAndProgress(Long courseEnrollmentId, LessonProgress progress);

  void updateProgress(Long lessonEnrollmentId, LessonProgress progress);

  Optional<Long> findLessonEnrollmentIdByLessonIdAndUserId(Long lessonId, Long userId);

  void saveAll(List<LessonEnrollmentModel> models);
}
