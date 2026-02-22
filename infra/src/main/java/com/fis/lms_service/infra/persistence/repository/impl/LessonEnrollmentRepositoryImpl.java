package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.enrollment.LessonEnrollmentModel;
import com.fis.lms_service.core.repository.enrollment.LessonEnrollmentRepository;
import com.fis.lms_service.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import com.fis.lms_service.infra.persistence.repository.jpa.CourseEnrollmentEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEnrollmentEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonEnrollmentRepositoryImpl implements LessonEnrollmentRepository {

  LessonEnrollmentEntityRepository lessonEnrollmentEntityRepository;
  CourseEnrollmentEntityRepository courseEnrollmentEntityRepository;
  LessonEntityRepository lessonEntityRepository;

  @Override
  public List<Long> findLessonIdsByCourseEnrollmentId(Long courseEnrollmentId) {
    return lessonEnrollmentEntityRepository.findLessonIdsByCourseEnrollmentId(courseEnrollmentId);
  }

  @Override
  public void saveAll(List<LessonEnrollmentModel> models) {
    if (models == null || models.isEmpty()) {
      return;
    }

    var courseEnrollment =
        courseEnrollmentEntityRepository.getReferenceById(models.get(0).getCourseEnrollmentId());
    List<LessonEnrollmentEntity> entities = new ArrayList<>(models.size());

    for (LessonEnrollmentModel model : models) {
      LessonEnrollmentEntity entity = new LessonEnrollmentEntity();
      entity.setCourseEnrollmentEntity(courseEnrollment);
      entity.setLessonEntity(lessonEntityRepository.getReferenceById(model.getLessonId()));
      entity.setLessonProgress(model.getLessonProgress());
      entities.add(entity);
    }

    lessonEnrollmentEntityRepository.saveAll(entities);
  }
}
