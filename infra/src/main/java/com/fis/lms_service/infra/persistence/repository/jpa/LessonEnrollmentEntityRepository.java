package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Admin 1/26/2026 */
@Repository
public interface LessonEnrollmentEntityRepository
    extends JpaRepository<@NonNull LessonEnrollmentEntity, @NonNull Long> {

  @Query(
      "select le.lessonEntity.lessonId from LessonEnrollmentEntity le "
          + "where le.courseEnrollmentEntity.courseEnrollmentId = :courseEnrollmentId")
  List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);
}
