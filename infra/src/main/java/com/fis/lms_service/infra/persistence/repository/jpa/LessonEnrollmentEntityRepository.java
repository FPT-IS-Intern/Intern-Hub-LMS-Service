package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.core.domain.model.enrollment.constant.LessonProgress;
import com.fis.lms_service.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 1/26/2026
 */
@Repository
public interface LessonEnrollmentEntityRepository
        extends JpaRepository<@NonNull LessonEnrollmentEntity, @NonNull Long> {

    @Query(
            "select le.lessonEntity.lessonId from LessonEnrollmentEntity le "
                    + "where le.courseEnrollmentEntity.courseEnrollmentId = :courseEnrollmentId")
    List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId);

    @Query(
            "select le.lessonEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.courseEnrollmentEntity.courseEnrollmentId = :courseEnrollmentId "
                    + "and le.lessonEntity.lessonId = :lessonId")
    Long findLessonEnrollmentId(Long courseEnrollmentId, Long lessonId);

    @Query(
            "select le.courseEnrollmentEntity.userId from LessonEnrollmentEntity le "
                    + "where le.lessonEnrollmentId = :lessonEnrollmentId")
    Long findUserIdByLessonEnrollmentId(Long lessonEnrollmentId);

    @Query(
            "select le.courseEnrollmentEntity.courseEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.lessonEnrollmentId = :lessonEnrollmentId")
    Long findCourseEnrollmentIdByLessonEnrollmentId(Long lessonEnrollmentId);

    long countByCourseEnrollmentEntity_CourseEnrollmentId(Long courseEnrollmentId);

    long countByCourseEnrollmentEntity_CourseEnrollmentIdAndLessonProgress(
            Long courseEnrollmentId, LessonProgress lessonProgress);

    @Query(
            "select le.lessonEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.lessonEntity.lessonId = :lessonId "
                    + "and le.courseEnrollmentEntity.userId = :userId")
    Long findLessonEnrollmentIdByLessonIdAndUserId(Long lessonId, Long userId);
}
