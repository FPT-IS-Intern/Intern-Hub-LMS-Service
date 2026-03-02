package com.intern.hub.core.domain.model.submission;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Admin 1/27/2026
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionAttachmentModel {

    Long submissionAttachmentId;
    Long lessonSubmissionId;
    String fileUrl;
    String fileName;
    Long fileSize;
}
