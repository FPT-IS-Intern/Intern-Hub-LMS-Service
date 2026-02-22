package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.submission.SubmissionCommentModel;
import com.fis.lms_service.core.repository.submission.SubmissionCommentRepository;
import com.fis.lms_service.infra.persistence.entity.submission.SubmissionCommentEntity;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonSubmissionEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.SubmissionCommentEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionCommentRepositoryImpl implements SubmissionCommentRepository {

  SubmissionCommentEntityRepository submissionCommentEntityRepository;
  LessonSubmissionEntityRepository lessonSubmissionEntityRepository;

  @Override
  public SubmissionCommentModel save(SubmissionCommentModel model) {
    SubmissionCommentEntity entity = new SubmissionCommentEntity();
    entity.setLessonSubmissionEntity(
        lessonSubmissionEntityRepository.getReferenceById(model.getLessonSubmissionId()));
    entity.setUserId(model.getUserId());
    entity.setContent(model.getContent());
    entity.setCommentAt(model.getCommentAt());

    SubmissionCommentEntity saved = submissionCommentEntityRepository.save(entity);

    return SubmissionCommentModel.builder()
        .submissionCommentId(saved.getSubmissionCommentId())
        .lessonSubmissionId(saved.getLessonSubmissionEntity().getLessonSubmissionId())
        .userId(saved.getUserId())
        .content(saved.getContent())
        .commentAt(saved.getCommentAt())
        .build();
  }
}
