package com.fis.lms_service.core.repository.submission;

import com.fis.lms_service.core.domain.model.submission.SubmissionAttachmentModel;

import java.util.List;

public interface SubmissionAttachmentRepository {

  void saveAll(List<SubmissionAttachmentModel> models);
}
