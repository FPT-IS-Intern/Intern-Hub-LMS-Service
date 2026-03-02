package com.intern.hub.core.repository.submission;

import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;

public interface SubmissionCommentRepository {

    SubmissionCommentModel save(SubmissionCommentModel model);
}
