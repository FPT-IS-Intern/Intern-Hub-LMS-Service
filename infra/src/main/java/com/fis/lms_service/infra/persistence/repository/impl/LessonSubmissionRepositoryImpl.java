package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.submission.LessonSubmissionModel;
import com.fis.lms_service.core.repository.submission.LessonSubmissionRepository;
import com.fis.lms_service.infra.persistence.entity.submission.LessonSubmissionEntity;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEnrollmentEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonSubmissionEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonSubmissionRepositoryImpl implements LessonSubmissionRepository {

  LessonSubmissionEntityRepository lessonSubmissionEntityRepository;
  LessonEnrollmentEntityRepository lessonEnrollmentEntityRepository;

  @Override
  public Optional<LessonSubmissionModel> findByLessonEnrollmentId(Long lessonEnrollmentId) {
    return lessonSubmissionEntityRepository
        .findByLessonEnrollmentEntity_LessonEnrollmentId(lessonEnrollmentId)
        .map(this::toModel);
  }

  @Override
  public LessonSubmissionModel save(LessonSubmissionModel model) {
    LessonSubmissionEntity entity;
    if (model.getLessonSubmissionId() == null) {
      entity = new LessonSubmissionEntity();
    } else {
      entity =
          lessonSubmissionEntityRepository
              .findById(model.getLessonSubmissionId())
              .orElse(new LessonSubmissionEntity());
    }

    entity.setLessonEnrollmentEntity(
        lessonEnrollmentEntityRepository.getReferenceById(model.getLessonEnrollmentId()));
    entity.setSubmissionStatus(model.getSubmissionStatus());
    entity.setLastSubmissionAt(model.getLastSubmissionAt());

    return toModel(lessonSubmissionEntityRepository.save(entity));
  }

  private LessonSubmissionModel toModel(LessonSubmissionEntity entity) {
    return LessonSubmissionModel.builder()
        .lessonSubmissionId(entity.getLessonSubmissionId())
        .lessonEnrollmentId(entity.getLessonEnrollmentEntity().getLessonEnrollmentId())
        .submissionStatus(entity.getSubmissionStatus())
        .lastSubmissionAt(entity.getLastSubmissionAt())
        .build();
  }
}
