package com.intern.hub.core.repository.enrollment;

import com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel;
import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;

import java.util.List;
import java.util.Optional;

public interface LessonEnrollmentRepository {

    List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);

    List<Long> findLessonEnrollmentIdsByCourseEnrollmentId(Long courseEnrollmentId);

    Optional<Long> findLessonEnrollmentId(Long courseEnrollmentId, Long lessonId);

    Optional<Long> findUserIdByLessonEnrollmentId(Long lessonEnrollmentId);

    Optional<Long> findCourseEnrollmentIdByLessonEnrollmentId(Long lessonEnrollmentId);

    long countByCourseEnrollmentId(Long courseEnrollmentId);

    long countByCourseEnrollmentIdAndProgress(Long courseEnrollmentId, LessonProgress progress);

    void updateProgress(Long lessonEnrollmentId, LessonProgress progress);

    Optional<Long> findLessonEnrollmentIdByLessonIdAndUserId(Long lessonId, Long userId);

    List<LessonEnrollmentModel> findAllByLessonIdAndUserId(Long lessonId, Long userId);

    Optional<LessonEnrollmentModel> findByLessonIdAndUserId(Long lessonId, Long userId);

    Optional<LessonEnrollmentModel> findByCourseEnrollmentIdAndLessonId(Long courseEnrollmentId, Long lessonId);

    void saveAll(List<LessonEnrollmentModel> models);
}
