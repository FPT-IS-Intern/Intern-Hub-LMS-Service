package com.fis.lms_service.core.repository.submission;

import com.fis.lms_service.core.domain.model.submission.LessonSubmissionModel;
import java.util.Optional;

public interface LessonSubmissionRepository {

  Optional<LessonSubmissionModel> findByLessonEnrollmentId(Long lessonEnrollmentId);

  LessonSubmissionModel save(LessonSubmissionModel model);
}
