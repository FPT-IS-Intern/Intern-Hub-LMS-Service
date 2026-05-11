package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.course.CourseEvaluatorEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEvaluatorEntityRepository
    extends JpaRepository<@NonNull CourseEvaluatorEntity, @NonNull Long> {

  void deleteByCourseEntity_CourseId(Long courseId);

  @Query(
      "select ce.userId from CourseEvaluatorEntity ce "
          + "where ce.courseEntity.courseId = :courseId "
          + "order by ce.createdAt asc")
  List<Long> findUserIdsByCourseId(@Param("courseId") Long courseId);

  @Query(
      value =
          """
                    SELECT
                      c.course_id AS courseId,
                      c.name AS name,
                      c.course_image_url AS courseImageUrl,
                      COUNT(en.course_enrollment_id) AS totalEnrollmentCount,
                      COALESCE(SUM(CASE WHEN en.course_progress = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completedEnrollmentCount,
                      CASE WHEN COUNT(ce.course_evaluator_id) > 0 THEN true ELSE false END AS canEvaluate
                    FROM courses c
                    LEFT JOIN course_enrollments en ON en.course_id = c.course_id
                    LEFT JOIN course_evaluators ce
                      ON ce.course_id = c.course_id AND ce.user_id = :userId
                    GROUP BY c.course_id, c.name, c.course_image_url, c.created_at
                    ORDER BY c.created_at DESC
                    """,
      countQuery = "SELECT COUNT(*) FROM courses c",
      nativeQuery = true)
  Page<CourseOverviewProjection> findAllCourseOverviews(
      @Param("userId") Long userId, Pageable pageable);

  @Query(
      value =
          """
                    SELECT
                      ce.course_id AS courseId,
                      c.name AS name,
                      c.course_image_url AS courseImageUrl,
                      COUNT(en.course_enrollment_id) AS totalEnrollmentCount,
                      COALESCE(SUM(CASE WHEN en.course_progress = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completedEnrollmentCount,
                      true AS canEvaluate
                    FROM course_evaluators ce
                    JOIN courses c ON c.course_id = ce.course_id
                    LEFT JOIN course_enrollments en ON en.course_id = ce.course_id
                    WHERE ce.user_id = :userId
                    GROUP BY ce.course_id, c.name, c.course_image_url, c.created_at
                    ORDER BY c.created_at DESC
                    """,
      countQuery = "SELECT COUNT(*) FROM course_evaluators ce WHERE ce.user_id = :userId",
      nativeQuery = true)
  Page<CourseOverviewProjection> findCourseOverviewsByEvaluatorUserId(
      @Param("userId") Long userId, Pageable pageable);

  interface CourseOverviewProjection {
    Long getCourseId();

    String getName();

    String getCourseImageUrl();

    Long getTotalEnrollmentCount();

    Long getCompletedEnrollmentCount();

    boolean getCanEvaluate();
  }
}
