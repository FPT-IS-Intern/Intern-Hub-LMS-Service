package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.repository.course.CourseEvaluatorAssignmentRepository;
import com.intern.hub.infra.persistence.entity.course.CourseEvaluatorEntity;
import com.intern.hub.infra.persistence.repository.jpa.CourseEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.CourseEvaluatorEntityRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEvaluatorAssignmentRepositoryImpl
    implements CourseEvaluatorAssignmentRepository {

  CourseEvaluatorEntityRepository courseEvaluatorEntityRepository;
  CourseEntityRepository courseEntityRepository;

  @Override
  public void saveCourseEvaluators(Long courseId, List<Long> evaluatorUserIds) {
    if (evaluatorUserIds == null || evaluatorUserIds.isEmpty()) {
      return;
    }

    var courseEntity = courseEntityRepository.getReferenceById(courseId);
    List<CourseEvaluatorEntity> entities = new ArrayList<>(evaluatorUserIds.size());
    for (Long evaluatorUserId : evaluatorUserIds) {
      if (evaluatorUserId == null) {
        continue;
      }
      CourseEvaluatorEntity entity = new CourseEvaluatorEntity();
      entity.setCourseEntity(courseEntity);
      entity.setUserId(evaluatorUserId);
      entities.add(entity);
    }

    if (!entities.isEmpty()) {
      courseEvaluatorEntityRepository.saveAll(entities);
    }
  }

  @Override
  public void replaceCourseEvaluators(Long courseId, List<Long> evaluatorUserIds) {
    courseEvaluatorEntityRepository.deleteByCourseEntity_CourseId(courseId);
    courseEvaluatorEntityRepository.flush();
    saveCourseEvaluators(courseId, evaluatorUserIds);
  }

  @Override
  public List<Long> findEvaluatorUserIdsByCourseId(Long courseId) {
    return courseEvaluatorEntityRepository.findUserIdsByCourseId(courseId);
  }
}
