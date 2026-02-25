package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.infra.persistence.entity.submission.SubmissionAttachmentEntity;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Admin 1/26/2026 */
@Repository
public interface SubmissionAttachmentEntityRepository
    extends JpaRepository<@NonNull SubmissionAttachmentEntity, @NonNull Long> {

  List<SubmissionAttachmentEntity> findByLessonSubmissionEntity_LessonSubmissionId(
      Long lessonSubmissionId);

  void deleteByLessonSubmissionEntity_LessonSubmissionId(Long lessonSubmissionId);
}
