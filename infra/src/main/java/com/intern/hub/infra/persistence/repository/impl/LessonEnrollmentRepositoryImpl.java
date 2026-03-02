package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel;
import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.infra.persistence.entity.enrollment.LessonEnrollmentEntity;
import com.intern.hub.infra.persistence.repository.jpa.CourseEnrollmentEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.LessonEnrollmentEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.LessonEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<Long> findLessonEnrollmentId(Long courseEnrollmentId, Long lessonId) {
        return Optional.ofNullable(
                lessonEnrollmentEntityRepository.findLessonEnrollmentId(courseEnrollmentId, lessonId));
    }

    @Override
    public Optional<Long> findUserIdByLessonEnrollmentId(Long lessonEnrollmentId) {
        return Optional.ofNullable(
                lessonEnrollmentEntityRepository.findUserIdByLessonEnrollmentId(lessonEnrollmentId));
    }

    @Override
    public Optional<Long> findCourseEnrollmentIdByLessonEnrollmentId(Long lessonEnrollmentId) {
        return Optional.ofNullable(
                lessonEnrollmentEntityRepository.findCourseEnrollmentIdByLessonEnrollmentId(
                        lessonEnrollmentId));
    }

    @Override
    public long countByCourseEnrollmentId(Long courseEnrollmentId) {
        return lessonEnrollmentEntityRepository.countByCourseEnrollmentEntity_CourseEnrollmentId(
                courseEnrollmentId);
    }

    @Override
    public long countByCourseEnrollmentIdAndProgress(
            Long courseEnrollmentId, LessonProgress progress) {
        return lessonEnrollmentEntityRepository
                .countByCourseEnrollmentEntity_CourseEnrollmentIdAndLessonProgress(
                        courseEnrollmentId, progress);
    }

    @Override
    public void updateProgress(Long lessonEnrollmentId, LessonProgress progress) {
        LessonEnrollmentEntity entity =
                lessonEnrollmentEntityRepository
                        .findById(lessonEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Lesson enrollment not found: " + lessonEnrollmentId));
        entity.setLessonProgress(progress);
        lessonEnrollmentEntityRepository.save(entity);
    }

    @Override
    public Optional<Long> findLessonEnrollmentIdByLessonIdAndUserId(Long lessonId, Long userId) {
        return Optional.ofNullable(
                lessonEnrollmentEntityRepository.findLessonEnrollmentIdByLessonIdAndUserId(
                        lessonId, userId));
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
