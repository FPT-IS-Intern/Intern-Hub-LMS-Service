package com.fis.lms_service.core.repository.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;

import java.util.List;
import java.util.Optional;

/**
 * Admin 1/29/2026
 */
public interface LessonFileRepository {

    void save(LessonFileModel lessonFileModel);

    Long getTotalSizeByLessonId(Long lessonId, LessonFileType lessonFileType);

    List<LessonFileModel> findAllByLessonId(Long lessonId);

    Optional<LessonFileModel> findById(Long lessonFileId);

    void deleteById(Long lessonFileId);
}
