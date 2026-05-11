package com.intern.hub.core.repository.submission;

import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;
import java.util.Optional;

public interface SubmissionCommentRepository {

  SubmissionCommentModel save(SubmissionCommentModel model);

  Optional<SubmissionCommentModel> findLatestByLessonSubmissionId(Long lessonSubmissionId);

  java.util.List<SubmissionCommentModel> findByLessonSubmissionIdOrderByCommentAtDesc(
      Long lessonSubmissionId);
}
