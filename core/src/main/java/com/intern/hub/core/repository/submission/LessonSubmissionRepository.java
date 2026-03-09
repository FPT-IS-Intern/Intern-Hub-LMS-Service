package com.intern.hub.core.repository.submission;

import com.intern.hub.core.domain.model.submission.LessonSubmissionModel;
import com.intern.hub.core.domain.model.submission.EvaluatorSubmissionOverviewModel;

import java.util.List;
import java.util.Optional;

public interface LessonSubmissionRepository {

    Optional<LessonSubmissionModel> findById(Long lessonSubmissionId);

    Optional<LessonSubmissionModel> findByLessonEnrollmentId(Long lessonEnrollmentId);

    List<EvaluatorSubmissionOverviewModel> findByCourseId(Long courseId);

    Optional<Long> findCourseIdByLessonSubmissionId(Long lessonSubmissionId);

    LessonSubmissionModel save(LessonSubmissionModel model);
}
