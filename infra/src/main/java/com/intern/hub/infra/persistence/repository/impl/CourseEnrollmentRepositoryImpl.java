package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.infra.persistence.entity.enrollment.CourseEnrollmentEntity;
import com.intern.hub.infra.persistence.repository.jpa.CourseEnrollmentEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.CourseEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEnrollmentRepositoryImpl implements CourseEnrollmentRepository {

    CourseEnrollmentEntityRepository courseEnrollmentEntityRepository;
    CourseEntityRepository courseEntityRepository;

    @Override
    public Optional<CourseEnrollmentModel> findByCourseIdAndUserId(Long courseId, Long userId) {
        return courseEnrollmentEntityRepository
                .findAllByCourseEntity_CourseIdAndUserIdOrderByCreatedAtDesc(courseId, userId)
                .stream()
                .findFirst()
                .map(this::toModel);
    }

    @Override
    public List<CourseEnrollmentModel> findAllByCourseId(Long courseId) {
        return courseEnrollmentEntityRepository.findAllByCourseEntity_CourseIdOrderByCreatedAtDesc(courseId).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public Optional<CourseEnrollmentModel> findById(Long courseEnrollmentId) {
        return courseEnrollmentEntityRepository.findById(courseEnrollmentId).map(this::toModel);
    }

    @Override
    public CourseEnrollmentModel save(CourseEnrollmentModel model) {
        CourseEnrollmentEntity entity;
        if (model.getCourseEnrollmentId() == null) {
            entity = new CourseEnrollmentEntity();
        } else {
            entity =
                    courseEnrollmentEntityRepository
                            .findById(model.getCourseEnrollmentId())
                            .orElse(new CourseEnrollmentEntity());
        }

        entity.setCourseEntity(courseEntityRepository.getReferenceById(model.getCourseId()));
        entity.setUserId(model.getUserId());
        entity.setCourseProgress(model.getCourseProgress());

        return toModel(courseEnrollmentEntityRepository.save(entity));
    }

    private CourseEnrollmentModel toModel(CourseEnrollmentEntity entity) {
        return CourseEnrollmentModel.builder()
                .courseEnrollmentId(entity.getCourseEnrollmentId())
                .courseId(entity.getCourseEntity().getCourseId())
                .userId(entity.getUserId())
                .courseProgress(entity.getCourseProgress())
                .build();
    }
}
