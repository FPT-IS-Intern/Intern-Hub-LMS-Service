package com.fis.lms_service.core.repository.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Admin 1/29/2026
 */
public interface LessonRepository {

    LessonModel save(LessonModel lessonModel);

    Optional<LessonModel> findById(Long lessonId);

    void deleteById(Long lessonId);

    Page<@NonNull LessonModel> findAll(Pageable pageable);

    void flush();
}
