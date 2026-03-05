package com.intern.hub.core.repository.submission;

import com.intern.hub.core.domain.model.submission.LessonSubmissionModel;

import java.util.Optional;

public interface LessonSubmissionRepository {

    Optional<LessonSubmissionModel> findByLessonEnrollmentId(Long lessonEnrollmentId);

    LessonSubmissionModel save(LessonSubmissionModel model);
}
