package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.submission.EvaluatorSubmissionOverviewModel;
import com.intern.hub.core.domain.model.submission.LessonSubmissionModel;
import com.intern.hub.core.repository.submission.LessonSubmissionRepository;
import com.intern.hub.infra.persistence.entity.submission.LessonSubmissionEntity;
import com.intern.hub.infra.persistence.repository.jpa.LessonEnrollmentEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.LessonSubmissionEntityRepository;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonSubmissionRepositoryImpl implements LessonSubmissionRepository {

  LessonSubmissionEntityRepository lessonSubmissionEntityRepository;
  LessonEnrollmentEntityRepository lessonEnrollmentEntityRepository;

  @Override
  public Optional<LessonSubmissionModel> findById(Long lessonSubmissionId) {
    return lessonSubmissionEntityRepository.findById(lessonSubmissionId).map(this::toModel);
  }

  @Override
  public Optional<LessonSubmissionModel> findByLessonEnrollmentId(Long lessonEnrollmentId) {
    return lessonSubmissionEntityRepository
        .findByLessonEnrollmentEntity_LessonEnrollmentId(lessonEnrollmentId)
        .map(this::toModel);
  }

  @Override
  public List<EvaluatorSubmissionOverviewModel> findByCourseId(Long courseId) {
    return lessonSubmissionEntityRepository.findByCourseId(courseId).stream()
        .map(
            item ->
                EvaluatorSubmissionOverviewModel.builder()
                    .lessonSubmissionId(item.getLessonSubmissionId())
                    .courseEnrollmentId(item.getCourseEnrollmentId())
                    .lessonEnrollmentId(item.getLessonEnrollmentId())
                    .lessonId(item.getLessonId())
                    .lessonName(item.getLessonName())
                    .userId(item.getUserId())
                    .submissionStatus(item.getSubmissionStatus())
                    .evaluationStatus(item.getEvaluationStatus())
                    .lastSubmissionAt(item.getLastSubmissionAt())
                    .build())
        .toList();
  }

  @Override
  public Optional<Long> findCourseIdByLessonSubmissionId(Long lessonSubmissionId) {
    return lessonSubmissionEntityRepository.findCourseIdByLessonSubmissionId(lessonSubmissionId);
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
    entity.setEvaluationStatus(model.getEvaluationStatus());
    entity.setLastSubmissionAt(model.getLastSubmissionAt());

    return toModel(lessonSubmissionEntityRepository.save(entity));
  }

  private LessonSubmissionModel toModel(LessonSubmissionEntity entity) {
    return LessonSubmissionModel.builder()
        .lessonSubmissionId(entity.getLessonSubmissionId())
        .lessonEnrollmentId(entity.getLessonEnrollmentEntity().getLessonEnrollmentId())
        .submissionStatus(entity.getSubmissionStatus())
        .evaluationStatus(entity.getEvaluationStatus())
        .lastSubmissionAt(entity.getLastSubmissionAt())
        .build();
  }
}
