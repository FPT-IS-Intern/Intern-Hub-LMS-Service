package com.intern.hub.core.service.submission;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionService {

    LessonSubmissionService lessonSubmissionService;

    @Transactional
    public LessonSubmissionService.LessonSubmissionResult submitLesson(
            Long lessonEnrollmentId,
            Long userId,
            Long actorId,
            String comment,
            List<Long> deleteAttachmentIds,
            List<MultipartFile> files) {
        return lessonSubmissionService.submitLesson(
                lessonEnrollmentId, userId, actorId, comment, deleteAttachmentIds, files);
    }

    @Transactional(readOnly = true)
    public LessonSubmissionService.LessonSubmissionResult getSubmission(Long lessonEnrollmentId) {
        return lessonSubmissionService.getSubmission(lessonEnrollmentId);
    }
}
