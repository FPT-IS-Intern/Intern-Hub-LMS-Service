package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.repository.course.CoursePositionAssignmentRepository;
import com.intern.hub.infra.persistence.entity.course.CoursePositionEntity;
import com.intern.hub.infra.persistence.repository.jpa.CourseEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.CoursePositionEntityRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CoursePositionAssignmentRepositoryImpl implements CoursePositionAssignmentRepository {

  CoursePositionEntityRepository coursePositionEntityRepository;
  CourseEntityRepository courseEntityRepository;

  @Override
  public void saveCoursePositions(Long courseId, List<Long> positionIds) {
    if (positionIds == null || positionIds.isEmpty()) {
      return;
    }

    var courseEntity = courseEntityRepository.getReferenceById(courseId);
    List<CoursePositionEntity> entities = new ArrayList<>(positionIds.size());
    for (Long positionId : positionIds) {
      if (positionId == null) {
        continue;
      }
      CoursePositionEntity entity = new CoursePositionEntity();
      entity.setCourseEntity(courseEntity);
      entity.setPositionId(positionId);
      entities.add(entity);
    }

    if (!entities.isEmpty()) {
      coursePositionEntityRepository.saveAll(entities);
    }
  }

  @Override
  public void replaceCoursePositions(Long courseId, List<Long> positionIds) {
    coursePositionEntityRepository.deleteByCourseEntity_CourseId(courseId);
    coursePositionEntityRepository.flush();
    saveCoursePositions(courseId, positionIds);
  }

  @Override
  public List<Long> findPositionIdsByCourseId(Long courseId) {
    return coursePositionEntityRepository.findPositionIdsByCourseId(courseId);
  }
}
