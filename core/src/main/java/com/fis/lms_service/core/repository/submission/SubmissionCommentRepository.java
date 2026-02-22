package com.fis.lms_service.core.repository.submission;

import com.fis.lms_service.core.domain.model.submission.SubmissionCommentModel;

public interface SubmissionCommentRepository {

  SubmissionCommentModel save(SubmissionCommentModel model);
}
