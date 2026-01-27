package com.fis.lms_service.core.domain.model.submission;

import com.fis.lms_service.core.domain.model.submission.constant.SubmissionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonSubmissionModel {

  Long lessonSubmissionId;
  Long lessonEnrollmentId;
  SubmissionStatus submissionStatus;
  Long lastSubmissionAt;
}
