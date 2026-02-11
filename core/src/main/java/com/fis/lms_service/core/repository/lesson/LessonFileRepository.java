package com.fis.lms_service.core.repository.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;

import java.util.List;

/**
 * Admin 1/29/2026
 */
public interface LessonFileRepository {

    void save(LessonFileModel lessonFileModel);

    Long getTotalSizeByLessonId(Long lessonId, LessonFileType lessonFileType);

    List<LessonFileModel> findAllByLessonId(Long lessonId);

    LessonFileModel findById(Long lessonFileId);

    void deleteById(Long lessonFileId);
}
