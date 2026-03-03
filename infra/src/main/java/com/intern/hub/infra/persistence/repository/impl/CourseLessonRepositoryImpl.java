package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.infra.persistence.entity.course.CourseLessonEntity;
import com.intern.hub.infra.persistence.repository.jpa.CourseEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.CourseLessonEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.LessonEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void replaceCourseLessons(Long courseId, List<Long> lessonIds) {
        courseLessonEntityRepository.deleteByCourseEntity_CourseId(courseId);
        courseLessonEntityRepository.flush();
        saveCourseLessons(courseId, lessonIds);
    }
}
