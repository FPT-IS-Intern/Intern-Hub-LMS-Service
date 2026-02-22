package com.fis.lms_service.core.service.submission;

import com.fis.lms_service.core.domain.model.submission.LessonSubmissionModel;
import com.fis.lms_service.core.domain.model.submission.SubmissionAttachmentModel;
import com.fis.lms_service.core.domain.model.submission.SubmissionCommentModel;
import com.fis.lms_service.core.domain.model.submission.constant.SubmissionStatus;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.enrollment.LessonEnrollmentRepository;
import com.fis.lms_service.core.repository.submission.LessonSubmissionRepository;
import com.fis.lms_service.core.repository.submission.SubmissionAttachmentRepository;
import com.fis.lms_service.core.repository.submission.SubmissionCommentRepository;
import com.fis.lms_service.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.BadRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonSubmissionService {

  LessonEnrollmentRepository lessonEnrollmentRepository;
  LessonSubmissionRepository lessonSubmissionRepository;
  SubmissionAttachmentRepository submissionAttachmentRepository;
  SubmissionCommentRepository submissionCommentRepository;
  FileStorageRepository fileStorageRepository;
  StorageObjectLifecycleManager storageObjectLifecycleManager;

  @NonFinal
  @Value("${aws.s3.paths.submission}")
  String submissionPath;

  @NonFinal
  @Value("${aws.s3.max-file-size}")
  Long maxFileSize;

  @NonFinal
  @Value("${aws.s3.allow-types.document}")
  String allowTypesDocument;

  @Transactional
  public void submitLesson(
      Long lessonEnrollmentId, Long userId, String comment, List<MultipartFile> files) {
    Long enrolledUserId =
        lessonEnrollmentRepository
            .findUserIdByLessonEnrollmentId(lessonEnrollmentId)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        "lesson.enrollment.not.found", "Không tìm thấy lesson enrollment"));

    if (!enrolledUserId.equals(userId)) {
      throw new BadRequestException(
          "lesson.enrollment.invalid", "Lesson enrollment không thuộc user này");
    }

    long now = System.currentTimeMillis();

    LessonSubmissionModel submission =
        lessonSubmissionRepository
            .findByLessonEnrollmentId(lessonEnrollmentId)
            .map(
                existing -> {
                  existing.setSubmissionStatus(SubmissionStatus.SUBMITTED);
                  existing.setLastSubmissionAt(now);
                  return lessonSubmissionRepository.save(existing);
                })
            .orElseGet(
                () ->
                    lessonSubmissionRepository.save(
                        LessonSubmissionModel.builder()
                            .lessonEnrollmentId(lessonEnrollmentId)
                            .submissionStatus(SubmissionStatus.SUBMITTED)
                            .lastSubmissionAt(now)
                            .build()));

    if (hasText(comment)) {
      submissionCommentRepository.save(
          SubmissionCommentModel.builder()
              .lessonSubmissionId(submission.getLessonSubmissionId())
              .userId(userId)
              .content(comment.trim())
              .commentAt(now)
              .build());
    }

    if (hasItems(files)) {
      List<SubmissionAttachmentModel> attachments = new ArrayList<>(files.size());
      for (MultipartFile file : files) {
        if (file == null || file.isEmpty()) {
          continue;
        }
        String key =
            fileStorageRepository.uploadFile(
                file,
                buildSubmissionPath(submission.getLessonSubmissionId()),
                maxFileSize,
                allowTypesDocument);
        storageObjectLifecycleManager.cleanupOnRollback(key);

        attachments.add(
            SubmissionAttachmentModel.builder()
                .lessonSubmissionId(submission.getLessonSubmissionId())
                .fileUrl(key)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .build());
      }

      if (!attachments.isEmpty()) {
        submissionAttachmentRepository.saveAll(attachments);
      }
    }
  }

  private String buildSubmissionPath(Long lessonSubmissionId) {
    return submissionPath + lessonSubmissionId;
  }

  private static boolean hasItems(List<?> items) {
    return items != null && !items.isEmpty();
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
