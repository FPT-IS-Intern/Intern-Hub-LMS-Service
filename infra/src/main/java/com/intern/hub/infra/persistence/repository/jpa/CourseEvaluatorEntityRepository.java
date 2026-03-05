package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.course.CourseEvaluatorEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CourseEvaluatorEntityRepository
        extends JpaRepository<@NonNull CourseEvaluatorEntity, @NonNull Long> {

    void deleteByCourseEntity_CourseId(Long courseId);

    @Query(
            """
            SELECT
              ce.courseEntity.courseId AS courseId,
              ce.courseEntity.name AS name,
              ce.courseEntity.courseImageUrl AS courseImageUrl,
              COUNT(en.courseEnrollmentId) AS totalEnrollmentCount,
              COALESCE(SUM(CASE WHEN en.courseProgress = com.intern.hub.core.domain.model.enrollment.constant.CourseProgress.COMPLETED THEN 1 ELSE 0 END), 0) AS completedEnrollmentCount
            FROM CourseEvaluatorEntity ce
            LEFT JOIN CourseEnrollmentEntity en ON en.courseEntity.courseId = ce.courseEntity.courseId
            WHERE ce.userId = :userId
            GROUP BY ce.courseEntity.courseId, ce.courseEntity.name, ce.courseEntity.courseImageUrl
            ORDER BY ce.courseEntity.createdAt DESC
            """)
    List<CourseOverviewProjection> findCourseOverviewsByEvaluatorUserId(@Param("userId") Long userId);

    interface CourseOverviewProjection {
        Long getCourseId();

        String getName();

        String getCourseImageUrl();

        Long getTotalEnrollmentCount();

        Long getCompletedEnrollmentCount();
    }
}
