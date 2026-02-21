package com.fis.lms_service.infra.persistence.entity.submission;

import com.fis.lms_service.core.domain.model.submission.constant.SubmissionStatus;
import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import com.fis.lms_service.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Entity
@Table(
    name = "lesson_submissions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"lesson_enrollment_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonSubmissionEntity extends AuditEntity {

  @Id
  @SnowflakeGenerated
  @Column(name = "lesson_submission_id", nullable = false, updatable = false)
  Long lessonSubmissionId;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_enrollment_id", nullable = false)
  LessonEnrollmentEntity lessonEnrollmentEntity;

  @Enumerated(EnumType.STRING)
  @Column(name = "submission_status", nullable = false)
  SubmissionStatus submissionStatus;

  @Column(name = "last_submission_at", nullable = false)
  Long lastSubmissionAt;
}
