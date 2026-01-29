package com.fis.lms_service.core.repository.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;

import java.util.List;

/**
 * Admin 1/29/2026
 *
 **/
public interface LessonFileRepository {

    void save(LessonFileModel model);

    Long getTotalSizeByLessonId(Long lessonId);

    List<LessonFileModel> findAllByLessonId(Long lessonId);

    LessonFileModel findByLessonFileId(Long lessonId);

    void deleteById(Long lessonFileId);

}
