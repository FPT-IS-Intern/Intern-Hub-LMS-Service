package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.repository.lesson.LessonRepository;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import com.fis.lms_service.infra.persistence.mapper.LessonMapper;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Admin 1/29/2026
 *
 **/
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonRepositoryImpl implements LessonRepository {

    LessonEntityRepository lessonEntityRepository;

    LessonMapper lessonMapper;

    @Override
    public LessonModel save(LessonModel lessonModel) {
        LessonEntity lessonEntity = lessonMapper.toEntity(lessonModel);

        LessonEntity savedLessonEntity = lessonEntityRepository.save(lessonEntity);

        return lessonMapper.toModel(savedLessonEntity);
    }

    @Override
    public Optional<LessonModel> findById(Long lessonId) {
        return lessonEntityRepository.findById(lessonId)
                .map(lessonMapper::toModel);
    }

    @Override
    public void deleteById(Long id) {
        lessonEntityRepository.deleteById(id);
    }
}
