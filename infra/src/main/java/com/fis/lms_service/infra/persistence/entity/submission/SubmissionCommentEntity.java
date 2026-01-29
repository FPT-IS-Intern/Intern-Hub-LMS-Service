package com.fis.lms_service.infra.persistence.entity.submission;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Entity
@Table(name = "submission_comments")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionCommentEntity extends AuditEntity {

  @Id
  @SnowflakeGenerated
  @Column(name = "submission_comment_id", nullable = false, updatable = false)
  Long submissionCommentId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_submission_id", nullable = false)
  LessonSubmissionEntity lessonSubmissionEntity;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @Column(name = "content", nullable = false, columnDefinition = "text")
  String content;

  @Column(name = "comment_at", nullable = false)
  Long commentAt;
}
