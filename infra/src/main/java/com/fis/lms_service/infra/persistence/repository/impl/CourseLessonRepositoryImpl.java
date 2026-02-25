package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.repository.course.CourseLessonRepository;
import com.fis.lms_service.infra.persistence.entity.course.CourseLessonEntity;
import com.fis.lms_service.infra.persistence.repository.jpa.CourseEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.CourseLessonEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEntityRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseLessonRepositoryImpl implements CourseLessonRepository {

  CourseLessonEntityRepository courseLessonEntityRepository;
  CourseEntityRepository courseEntityRepository;
  LessonEntityRepository lessonEntityRepository;

  @Override
  public List<Long> findLessonIdsByCourseId(Long courseId) {
    return courseLessonEntityRepository.findLessonIdsByCourseId(courseId);
  }

  @Override
  public void saveCourseLessons(Long courseId, List<Long> lessonIds) {
    if (lessonIds == null || lessonIds.isEmpty()) {
      return;
    }

    var courseEntity = courseEntityRepository.getReferenceById(courseId);
    List<CourseLessonEntity> entities = new ArrayList<>(lessonIds.size());

    int orderIndex = 1;
    for (Long lessonId : lessonIds) {
      if (lessonId == null) {
        continue;
      }
      CourseLessonEntity entity = new CourseLessonEntity();
      entity.setCourseEntity(courseEntity);
      entity.setLessonEntity(lessonEntityRepository.getReferenceById(lessonId));
      entity.setOrderIndex(orderIndex++);
      entities.add(entity);
    }

    if (!entities.isEmpty()) {
      courseLessonEntityRepository.saveAll(entities);
    }
  }
}
