package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.submission.LessonSubmissionEntity;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Admin 1/26/2026 */
@Repository
public interface LessonSubmissionEntityRepository
    extends JpaRepository<@NonNull LessonSubmissionEntity, @NonNull Long> {

  @Modifying
  @Query(
      "DELETE FROM LessonSubmissionEntity ls WHERE ls.lessonEnrollmentEntity.courseEnrollmentEntity.courseEntity.courseId = :courseId")
  void deleteByCourseId(@Param("courseId") Long courseId);

  Optional<LessonSubmissionEntity> findByLessonEnrollmentEntity_LessonEnrollmentId(
      Long lessonEnrollmentId);

  @Query(
      """
            SELECT ce.courseEntity.courseId
            FROM LessonSubmissionEntity ls
            JOIN ls.lessonEnrollmentEntity le
            JOIN le.courseEnrollmentEntity ce
            WHERE ls.lessonSubmissionId = :lessonSubmissionId
            """)
  Optional<Long> findCourseIdByLessonSubmissionId(
      @Param("lessonSubmissionId") Long lessonSubmissionId);

  @Query(
      """
            SELECT
              ls.lessonSubmissionId AS lessonSubmissionId,
              le.lessonEnrollmentId AS lessonEnrollmentId,
              ce.courseEnrollmentId AS courseEnrollmentId,
              l.lessonId AS lessonId,
              l.name AS lessonName,
              ce.userId AS userId,
              ls.submissionStatus AS submissionStatus,
              ls.evaluationStatus AS evaluationStatus,
              ls.lastSubmissionAt AS lastSubmissionAt
            FROM LessonSubmissionEntity ls
            JOIN ls.lessonEnrollmentEntity le
            JOIN le.courseEnrollmentEntity ce
            JOIN le.lessonEntity l
            WHERE ce.courseEntity.courseId = :courseId
            ORDER BY ls.lastSubmissionAt DESC, ls.lessonSubmissionId DESC
            """)
  List<CourseSubmissionProjection> findByCourseId(@Param("courseId") Long courseId);

  interface CourseSubmissionProjection {
    Long getLessonSubmissionId();

    Long getLessonEnrollmentId();

    Long getCourseEnrollmentId();

    Long getLessonId();

    String getLessonName();

    Long getUserId();

    com.intern.hub.core.domain.model.submission.constant.SubmissionStatus getSubmissionStatus();

    com.intern.hub.core.domain.model.submission.constant.SubmissionEvaluationStatus
        getEvaluationStatus();

    Long getLastSubmissionAt();
  }
}
