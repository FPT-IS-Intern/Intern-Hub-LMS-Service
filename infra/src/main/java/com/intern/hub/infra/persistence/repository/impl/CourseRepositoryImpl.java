package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.course.CourseModel;
import com.intern.hub.core.repository.course.CourseRepository;
import com.intern.hub.infra.persistence.entity.course.CourseEntity;
import com.intern.hub.infra.persistence.mapper.CourseEntityMapper;
import com.intern.hub.infra.persistence.repository.jpa.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseRepositoryImpl implements CourseRepository {

    CourseEntityRepository courseEntityRepository;
    CourseEntityMapper courseEntityMapper;
    CourseLessonEntityRepository courseLessonEntityRepository;
    CourseEvaluatorEntityRepository courseEvaluatorEntityRepository;
    CoursePositionEntityRepository coursePositionEntityRepository;
    CourseEnrollmentEntityRepository courseEnrollmentEntityRepository;

    @Override
    public CourseModel save(CourseModel courseModel) {
        CourseEntity courseEntity;
        if (courseModel.getCourseId() == null) {
            courseEntity = new CourseEntity();
        } else {
            courseEntity =
                    courseEntityRepository.findById(courseModel.getCourseId()).orElse(new CourseEntity());
        }

        courseEntityMapper.updateEntityFromModel(courseModel, courseEntity);
        return courseEntityMapper.toModel(courseEntityRepository.save(courseEntity));
    }

    @Override
    public Optional<CourseModel> findById(Long courseId) {
        return courseEntityRepository.findById(courseId).map(courseEntityMapper::toModel);
    }

    @Override
    public void deleteById(Long courseId) {
        courseEntityRepository.deleteById(courseId);
    }

    @Override
    public void deleteWithRelationsById(Long courseId) {
        courseEnrollmentEntityRepository.deleteByCourseEntity_CourseId(courseId);
        courseEvaluatorEntityRepository.deleteByCourseEntity_CourseId(courseId);
        coursePositionEntityRepository.deleteByCourseEntity_CourseId(courseId);
        courseLessonEntityRepository.deleteByCourseEntity_CourseId(courseId);
        courseEntityRepository.deleteById(courseId);
    }

    @Override
    public Page<@NonNull CourseModel> findAll(Pageable pageable) {
        return courseEntityRepository.findAll(pageable).map(courseEntityMapper::toModel);
    }
}
