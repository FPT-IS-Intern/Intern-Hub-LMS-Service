package com.intern.hub.core.service.submission;

import com.intern.hub.core.domain.model.submission.LessonSubmissionModel;
import com.intern.hub.core.domain.model.submission.SubmissionAttachmentModel;
import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;
import com.intern.hub.core.domain.model.submission.constant.SubmissionStatus;
import com.intern.hub.core.domain.model.submission.constant.SubmissionEvaluationStatus;
import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.core.repository.submission.LessonSubmissionRepository;
import com.intern.hub.core.repository.submission.SubmissionAttachmentRepository;
import com.intern.hub.core.repository.submission.SubmissionCommentRepository;
import com.intern.hub.core.service.enrollment.EnrollmentProgressService;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** Nghiệp vụ nộp bài: kiểm tra quyền, thay file, cập nhật tiến độ lesson/course. */
public class LessonSubmissionService {

    public record LessonSubmissionResult(
            Long lessonSubmissionId,
            Long lessonEnrollmentId,
            SubmissionStatus submissionStatus,
            SubmissionEvaluationStatus evaluationStatus,
            Long lastSubmissionAt,
            String comment,
            String evaluatorComment,
            List<SubmissionAttachmentModel> attachments) {
    }

    LessonEnrollmentRepository lessonEnrollmentRepository;
    CourseEnrollmentRepository courseEnrollmentRepository;
    CourseLessonRepository courseLessonRepository;
    LessonSubmissionRepository lessonSubmissionRepository;
    SubmissionAttachmentRepository submissionAttachmentRepository;
    SubmissionCommentRepository submissionCommentRepository;
    EnrollmentProgressService enrollmentProgressService;
    FileStorageRepository fileStorageRepository;
    StorageObjectLifecycleManager storageObjectLifecycleManager;

    @NonFinal
    @Value("${aws.s3.paths.submission}")
    String submissionPath;

    @NonFinal
    @Value("${aws.s3.max-total-size}")
    Long maxTotalSize;

    @NonFinal
    @Value("${aws.s3.allow-types.document}")
    String allowTypesDocument;

    @Transactional
    /** Nộp hoặc cập nhật bài nộp của một lesson enrollment. */
    public LessonSubmissionResult submitLesson(
            Long lessonEnrollmentId,
            Long userId,
            Long actorId,
            String comment,
            List<Long> deleteAttachmentIds,
            List<MultipartFile> files) {
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
                                    existing.setEvaluationStatus(SubmissionEvaluationStatus.PENDING);
                                    existing.setLastSubmissionAt(now);
                                    return lessonSubmissionRepository.save(existing);
                                })
                        .orElseGet(
                                () ->
                                        lessonSubmissionRepository.save(
                                                LessonSubmissionModel.builder()
                                                        .lessonEnrollmentId(lessonEnrollmentId)
                                                        .submissionStatus(SubmissionStatus.SUBMITTED)
                                                        .evaluationStatus(SubmissionEvaluationStatus.PENDING)
                                                        .lastSubmissionAt(now)
                                                        .build()));

        List<SubmissionAttachmentModel> existingAttachments =
                submissionAttachmentRepository.findByLessonSubmissionId(submission.getLessonSubmissionId());
        boolean hasValidFile = hasValidFiles(files);
        List<SubmissionAttachmentModel> attachmentsToDelete = List.of();
        if (existingAttachments.isEmpty() && !hasValidFile) {
            throw new BadRequestException("submission.file.required", "Cần ít nhất 1 file để nộp bài");
        }

        if (hasText(comment)) {
            submissionCommentRepository.save(
                    SubmissionCommentModel.builder()
                            .lessonSubmissionId(submission.getLessonSubmissionId())
                            .userId(userId)
                            .content(comment.trim())
                            .commentAt(now)
                            .build());
        }

        if (hasItems(deleteAttachmentIds)) {
            attachmentsToDelete =
                    existingAttachments.stream()
                            .filter(
                                    attachment ->
                                            attachment.getSubmissionAttachmentId() != null
                                                    && deleteAttachmentIds.contains(
                                                            attachment.getSubmissionAttachmentId()))
                            .toList();

            if (!attachmentsToDelete.isEmpty()) {
                submissionAttachmentRepository.deleteByIds(
                        attachmentsToDelete.stream()
                                .map(SubmissionAttachmentModel::getSubmissionAttachmentId)
                                .toList());

                for (SubmissionAttachmentModel attachment : attachmentsToDelete) {
                    if (hasText(attachment.getFileUrl())) {
                        storageObjectLifecycleManager.deleteAfterCommit(attachment.getFileUrl(), actorId);
                    }
                }
            }
        }
        final Set<Long> deletedAttachmentIds =
                attachmentsToDelete.stream()
                        .map(SubmissionAttachmentModel::getSubmissionAttachmentId)
                        .collect(java.util.stream.Collectors.toSet());

        long totalRemainingSize =
                existingAttachments.stream()
                        .filter(
                                attachment ->
                                        attachment.getSubmissionAttachmentId() == null
                                                || !deletedAttachmentIds.contains(
                                                        attachment.getSubmissionAttachmentId()))
                        .map(SubmissionAttachmentModel::getFileSize)
                        .filter(size -> size != null && size > 0)
                        .reduce(0L, Long::sum);
        long totalNewFileSize =
                files == null
                        ? 0L
                        : files.stream()
                                .filter(file -> file != null && !file.isEmpty())
                                .map(MultipartFile::getSize)
                                .reduce(0L, Long::sum);
        if (totalRemainingSize + totalNewFileSize > maxTotalSize) {
            throw new BadRequestException(
                    "submission.total.size.exceeded",
                    "Tổng dung lượng bài nộp không được vượt quá " + (maxTotalSize / (1024 * 1024)) + "MB");
        }

        if (hasValidFile) {
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
                                maxTotalSize,
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

        List<SubmissionAttachmentModel> attachments =
                submissionAttachmentRepository.findByLessonSubmissionId(submission.getLessonSubmissionId());
        if (attachments.isEmpty()) {
            throw new BadRequestException(
                    "submission.file.required", "Bài nộp phải còn ít nhất 1 file đính kèm");
        }

        enrollmentProgressService.updateLessonProgressAndSyncCourse(
                lessonEnrollmentId, com.intern.hub.core.domain.model.enrollment.constant.LessonProgress.IN_PROGRESS);

        return new LessonSubmissionResult(
                submission.getLessonSubmissionId(),
                submission.getLessonEnrollmentId(),
                submission.getSubmissionStatus(),
                submission.getEvaluationStatus(),
                submission.getLastSubmissionAt(),
                hasText(comment) ? comment.trim() : null,
                null,
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

        var comments =
                submissionCommentRepository.findByLessonSubmissionIdOrderByCommentAtDesc(
                        submission.getLessonSubmissionId());
        Long learnerUserId =
                lessonEnrollmentRepository
                        .findUserIdByLessonEnrollmentId(lessonEnrollmentId)
                        .orElse(null);
        String latestComment =
                comments.stream()
                        .filter(comment -> java.util.Objects.equals(comment.getUserId(), learnerUserId))
                        .map(SubmissionCommentModel::getContent)
                        .findFirst()
                        .orElse(null);
        String evaluatorComment =
                comments.stream()
                        .filter(comment -> !java.util.Objects.equals(comment.getUserId(), learnerUserId))
                        .map(SubmissionCommentModel::getContent)
                        .findFirst()
                        .orElse(null);

        return new LessonSubmissionResult(
                submission.getLessonSubmissionId(),
                submission.getLessonEnrollmentId(),
                submission.getSubmissionStatus(),
                submission.getEvaluationStatus(),
                submission.getLastSubmissionAt(),
                latestComment,
                evaluatorComment,
                attachments);
    }

    @Transactional(readOnly = true)
    public List<LessonSubmissionResult> getSubmissionsByCourseEnrollment(Long courseEnrollmentId, Long userId) {
        var courseEnrollment =
                courseEnrollmentRepository
                        .findById(courseEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.enrollment.not.found", "Khong tim thay course enrollment"));
        if (!courseEnrollment.getUserId().equals(userId)) {
            throw new ForbiddenException(
                    "course.enrollment.invalid", "Course enrollment khong thuoc user nay");
        }

        return courseLessonRepository.findLessonIdsByCourseId(courseEnrollment.getCourseId()).stream()
                .map(lessonId -> lessonEnrollmentRepository.findLessonEnrollmentId(courseEnrollmentId, lessonId))
                .flatMap(java.util.Optional::stream)
                .map(lessonSubmissionRepository::findByLessonEnrollmentId)
                .flatMap(java.util.Optional::stream)
                .map(
                        submission -> {
                            List<SubmissionAttachmentModel> attachments =
                                    submissionAttachmentRepository.findByLessonSubmissionId(
                                            submission.getLessonSubmissionId());
                            String latestComment =
                                    submissionCommentRepository
                                            .findByLessonSubmissionIdOrderByCommentAtDesc(
                                                    submission.getLessonSubmissionId())
                                            .stream()
                                            .filter(comment -> java.util.Objects.equals(comment.getUserId(), userId))
                                            .map(SubmissionCommentModel::getContent)
                                            .findFirst()
                                            .orElse(null);
                            String evaluatorComment =
                                    submissionCommentRepository
                                            .findByLessonSubmissionIdOrderByCommentAtDesc(
                                                    submission.getLessonSubmissionId())
                                            .stream()
                                            .filter(comment -> !java.util.Objects.equals(comment.getUserId(), userId))
                                            .map(SubmissionCommentModel::getContent)
                                            .findFirst()
                                            .orElse(null);
                            return new LessonSubmissionResult(
                                    submission.getLessonSubmissionId(),
                                    submission.getLessonEnrollmentId(),
                                    submission.getSubmissionStatus(),
                                    submission.getEvaluationStatus(),
                                    submission.getLastSubmissionAt(),
                                    latestComment,
                                    evaluatorComment,
                                    attachments);
                        })
                .toList();
    }

    private String buildSubmissionPath(Long lessonSubmissionId) {
        return submissionPath + lessonSubmissionId;
    }

    private static boolean hasItems(List<?> items) {
        return items != null && !items.isEmpty();
    }

    private static boolean hasValidFiles(List<MultipartFile> files) {
        return files != null && files.stream().anyMatch(file -> file != null && !file.isEmpty());
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
