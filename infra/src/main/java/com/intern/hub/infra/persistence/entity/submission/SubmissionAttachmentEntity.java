package com.intern.hub.infra.persistence.entity.submission;

import com.intern.hub.infra.generator.SnowflakeGenerated;
import com.intern.hub.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Entity
@Table(name = "submission_attachments")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionAttachmentEntity extends AuditEntity {

  @Id
  @SnowflakeGenerated
  @Column(name = "submission_attachment_id", nullable = false, updatable = false)
  Long submissionAttachmentId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "lesson_submission_id", nullable = false)
  LessonSubmissionEntity lessonSubmissionEntity;

  @Column(name = "file_url", nullable = false, columnDefinition = "text")
  String fileUrl;

  @Column(name = "file_name", nullable = false, columnDefinition = "text")
  String fileName;

  @Column(name = "file_size", nullable = false)
  Long fileSize;
}
