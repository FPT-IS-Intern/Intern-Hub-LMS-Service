package com.intern.hub.core.domain.model.submission;

import com.intern.hub.core.domain.model.submission.constant.SubmissionEvaluationStatus;
import com.intern.hub.core.domain.model.submission.constant.SubmissionStatus;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluatorSubmissionOverviewModel {

  Long lessonSubmissionId;
  Long courseEnrollmentId;
  Long lessonEnrollmentId;
  Long lessonId;
  String lessonName;
  Long userId;
  String userEmail;
  String userFullName;
  String userAvatarUrl;
  SubmissionStatus submissionStatus;
  SubmissionEvaluationStatus evaluationStatus;
  Long lastSubmissionAt;
  String learnerNote;
  String evaluatorComment;
  List<SubmissionAttachmentModel> attachments;
}
