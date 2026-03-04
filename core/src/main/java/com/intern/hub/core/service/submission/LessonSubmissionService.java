package com.intern.hub.core.service.submission;

import com.intern.hub.core.domain.model.enrollment.constant.CourseProgress;
import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import com.intern.hub.core.domain.model.submission.LessonSubmissionModel;
import com.intern.hub.core.domain.model.submission.SubmissionAttachmentModel;
import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;
import com.intern.hub.core.domain.model.submission.constant.SubmissionStatus;
import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.core.repository.submission.LessonSubmissionRepository;
import com.intern.hub.core.repository.submission.SubmissionAttachmentRepository;
import com.intern.hub.core.repository.submission.SubmissionCommentRepository;
import com.intern.hub.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ForbiddenException;
import com.intern.hub.library.common.exception.NotFoundException;
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
/** Nghiệp vụ nộp bài: kiểm tra quyền, thay file, cập nhật tiến độ lesson/course. */
public class LessonSubmissionService {

    public record LessonSubmissionResult(
            Long lessonSubmissionId,
            Long lessonEnrollmentId,
            SubmissionStatus submissionStatus,
            Long lastSubmissionAt,
            String comment,
            List<SubmissionAttachmentModel> attachments) {
    }

    CourseEnrollmentRepository courseEnrollmentRepository;
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
    /** Nộp hoặc cập nhật bài nộp của một lesson enrollment. */
    public LessonSubmissionResult submitLesson(
            Long lessonEnrollmentId, Long userId, Long actorId, String comment, List<MultipartFile> files) {
        if (!hasItems(files)) {
            throw new BadRequestException("submission.file.required", "Cần ít nhất 1 file để nộp bài");
        }

        Long enrolledUserId =
                lessonEnrollmentRepository
                        .findUserIdByLessonEnrollmentId(lessonEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "lesson.enrollment.not.found", "Không tìm thấy lesson enrollment"));

        if (!enrolledUserId.equals(userId)) {
            throw new ForbiddenException(
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
            boolean hasValidFile = files.stream().anyMatch(file -> file != null && !file.isEmpty());
            if (!hasValidFile) {
                throw new BadRequestException("submission.file.required", "Cần ít nhất 1 file để nộp bài");
            }

            List<SubmissionAttachmentModel> existing =
                    submissionAttachmentRepository.findByLessonSubmissionId(
                            submission.getLessonSubmissionId());
            if (!existing.isEmpty()) {
                    submissionAttachmentRepository.deleteByLessonSubmissionId(
                        submission.getLessonSubmissionId());
                for (SubmissionAttachmentModel attachment : existing) {
                    if (hasText(attachment.getFileUrl())) {
                        storageObjectLifecycleManager.deleteAfterCommit(attachment.getFileUrl(), actorId);
                    }
                }
            }

            List<SubmissionAttachmentModel> attachments = new ArrayList<>(files.size());
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                String key =
                        fileStorageRepository.uploadFile(
                                file,
                                buildSubmissionPath(submission.getLessonSubmissionId()),
                                actorId,
                                maxFileSize,
                                allowTypesDocument);
                storageObjectLifecycleManager.cleanupOnRollback(key, actorId);

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

        lessonEnrollmentRepository.updateProgress(lessonEnrollmentId, LessonProgress.COMPLETED);

        Long courseEnrollmentId =
                lessonEnrollmentRepository
                        .findCourseEnrollmentIdByLessonEnrollmentId(lessonEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.enrollment.not.found", "Không tìm thấy course enrollment"));

        long totalLessons = lessonEnrollmentRepository.countByCourseEnrollmentId(courseEnrollmentId);
        long completedLessons =
                lessonEnrollmentRepository.countByCourseEnrollmentIdAndProgress(
                        courseEnrollmentId, LessonProgress.COMPLETED);
        CourseProgress courseProgress =
                totalLessons > 0 && completedLessons == totalLessons
                        ? CourseProgress.COMPLETED
                        : CourseProgress.IN_PROGRESS;

        courseEnrollmentRepository
                .findById(courseEnrollmentId)
                .ifPresent(
                        existing -> {
                            if (existing.getCourseProgress() != courseProgress) {
                                existing.setCourseProgress(courseProgress);
                                courseEnrollmentRepository.save(existing);
                            }
                        });

        List<SubmissionAttachmentModel> attachments =
                submissionAttachmentRepository.findByLessonSubmissionId(submission.getLessonSubmissionId());

        return new LessonSubmissionResult(
                submission.getLessonSubmissionId(),
                submission.getLessonEnrollmentId(),
                submission.getSubmissionStatus(),
                submission.getLastSubmissionAt(),
                hasText(comment) ? comment.trim() : null,
                attachments);
    }

    @Transactional(readOnly = true)
    /** Lấy bài nộp hiện tại của một lesson enrollment. */
    public LessonSubmissionResult getSubmission(Long lessonEnrollmentId) {
        LessonSubmissionModel submission =
                lessonSubmissionRepository
                        .findByLessonEnrollmentId(lessonEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "lesson.submission.not.found", "Không tìm thấy bài nộp"));

        List<SubmissionAttachmentModel> attachments =
                submissionAttachmentRepository.findByLessonSubmissionId(submission.getLessonSubmissionId());

        String latestComment =
                submissionCommentRepository
                        .findLatestByLessonSubmissionId(submission.getLessonSubmissionId())
                        .map(SubmissionCommentModel::getContent)
                        .orElse(null);

        return new LessonSubmissionResult(
                submission.getLessonSubmissionId(),
                submission.getLessonEnrollmentId(),
                submission.getSubmissionStatus(),
                submission.getLastSubmissionAt(),
                latestComment,
                attachments);
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
