package com.fis.lms_service.core.repository.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;

import java.util.Optional;

/**
 * Admin 1/29/2026
 */
public interface LessonRepository {

    LessonModel save(LessonModel lessonModel);

    Optional<LessonModel> findById(Long lessonId);

    void deleteById(Long lessonId);
}
