package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import com.intern.hub.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Long> findLessonIdsByCourseEnrollmentId(
            @Param("courseEnrollmentId") Long courseEnrollmentId);

    @Query(
            "select le.lessonEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.courseEnrollmentEntity.courseEnrollmentId = :courseEnrollmentId "
                    + "and le.lessonEntity.lessonId = :lessonId")
    Long findLessonEnrollmentId(
            @Param("courseEnrollmentId") Long courseEnrollmentId, @Param("lessonId") Long lessonId);

    @Query(
            "select le.courseEnrollmentEntity.userId from LessonEnrollmentEntity le "
                    + "where le.lessonEnrollmentId = :lessonEnrollmentId")
    Long findUserIdByLessonEnrollmentId(@Param("lessonEnrollmentId") Long lessonEnrollmentId);

    @Query(
            "select le.courseEnrollmentEntity.courseEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.lessonEnrollmentId = :lessonEnrollmentId")
    Long findCourseEnrollmentIdByLessonEnrollmentId(
            @Param("lessonEnrollmentId") Long lessonEnrollmentId);

    long countByCourseEnrollmentEntity_CourseEnrollmentId(Long courseEnrollmentId);

    long countByCourseEnrollmentEntity_CourseEnrollmentIdAndLessonProgress(
            Long courseEnrollmentId, LessonProgress lessonProgress);

    @Query(
            "select le.lessonEnrollmentId from LessonEnrollmentEntity le "
                    + "where le.lessonEntity.lessonId = :lessonId "
                    + "and le.courseEnrollmentEntity.userId = :userId")
    Long findLessonEnrollmentIdByLessonIdAndUserId(
            @Param("lessonId") Long lessonId, @Param("userId") Long userId);

    java.util.Optional<LessonEnrollmentEntity> findByLessonEntity_LessonIdAndCourseEnrollmentEntity_UserId(
            Long lessonId, Long userId);
}
