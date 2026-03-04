package com.intern.hub.core.service.lesson;

import com.intern.hub.core.domain.model.lesson.LessonFileModel;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {

    LessonQueryService lessonQueryService;
    LessonFileService lessonFileService;

    @Transactional(readOnly = true)
    public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
        return lessonQueryService.getLessons(pageable);
    }

    @Transactional(readOnly = true)
    public LessonModel getLesson(Long lessonId) {
        return lessonQueryService.getLesson(lessonId);
    }

    @Transactional(readOnly = true)
    public Long getLessonEnrollmentId(Long lessonId, Long userId) {
        return lessonQueryService.getLessonEnrollmentId(lessonId, userId);
    }

    @Transactional(readOnly = true)
    public List<LessonFileModel> getLessonFiles(Long lessonId) {
        return lessonFileService.getFiles(lessonId);
    }
}
