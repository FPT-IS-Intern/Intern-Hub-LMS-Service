package com.fis.lms_service.core.domain.model.submission;

import lombok.*;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionCommentModel {

  Long submissionCommentId;
  Long lessonSubmissionId;
  Long userId;
  String content;
  Long commentAt;
}
