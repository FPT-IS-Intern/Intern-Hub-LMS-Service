package com.intern.hub.core.repository.submission;

import com.intern.hub.core.domain.model.submission.SubmissionAttachmentModel;
import java.util.List;

public interface SubmissionAttachmentRepository {

  List<SubmissionAttachmentModel> findByLessonSubmissionId(Long lessonSubmissionId);

  void deleteByLessonSubmissionId(Long lessonSubmissionId);

  void deleteByIds(List<Long> submissionAttachmentIds);

  void saveAll(List<SubmissionAttachmentModel> models);
}
