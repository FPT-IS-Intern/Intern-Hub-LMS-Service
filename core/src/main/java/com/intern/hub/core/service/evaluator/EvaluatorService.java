package com.intern.hub.core.service.evaluator;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import com.intern.hub.core.domain.model.submission.EvaluatorSubmissionOverviewModel;
import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;
import com.intern.hub.core.domain.model.submission.constant.SubmissionEvaluationStatus;
import com.intern.hub.core.repository.course.CourseEvaluatorRepository;
import com.intern.hub.core.repository.course.CourseEvaluatorAssignmentRepository;
import com.intern.hub.core.repository.submission.LessonSubmissionRepository;
import com.intern.hub.core.repository.submission.SubmissionAttachmentRepository;
import com.intern.hub.core.repository.submission.SubmissionCommentRepository;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.core.service.enrollment.EnrollmentProgressService;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.NotFoundException;
import com.intern.hub.library.common.exception.ForbiddenException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluatorService {

    CourseEvaluatorRepository courseEvaluatorRepository;
    CourseEvaluatorAssignmentRepository courseEvaluatorAssignmentRepository;
    UserDirectoryRepository userDirectoryRepository;
    LessonSubmissionRepository lessonSubmissionRepository;
    SubmissionAttachmentRepository submissionAttachmentRepository;
    SubmissionCommentRepository submissionCommentRepository;
    EnrollmentProgressService enrollmentProgressService;

    @Transactional(readOnly = true)
    public Page<EvaluatorCourseOverviewModel> getCourseOverviews(
            Long evaluatorUserId, boolean onlyEvaluable, Pageable pageable) {
        if (onlyEvaluable) {
            if (!userDirectoryRepository.existsByUserId(evaluatorUserId)) {
                throw new NotFoundException("hrm.user.not.found", "Khong tim thay user trong HRM");
            }
            return courseEvaluatorRepository.findCourseOverviewsByEvaluatorUserId(evaluatorUserId, pageable);
        }
        return courseEvaluatorRepository.findAllCourseOverviews(evaluatorUserId, pageable);
    }

    @Transactional(readOnly = true)
    public java.util.List<EvaluatorSubmissionOverviewModel> getCourseSubmissions(
            Long courseId, Long evaluatorUserId) {
        var evaluatorUserIds = courseEvaluatorAssignmentRepository.findEvaluatorUserIdsByCourseId(courseId);
        if (evaluatorUserIds.stream().noneMatch(evaluatorUserId::equals)) {
            throw new ForbiddenException("evaluator.course.forbidden", "Ban khong duoc phan cong danh gia khoa hoc nay");
        }

        var submissions = lessonSubmissionRepository.findByCourseId(courseId);
        var users = userDirectoryRepository.findByIds(
                submissions.stream().map(EvaluatorSubmissionOverviewModel::getUserId).distinct().toList());
        var userMap = users.stream().collect(java.util.stream.Collectors.toMap(
                user -> user.getUserId(),
                user -> user));

        return submissions.stream()
                .map(
                        item -> {
                            var user = userMap.get(item.getUserId());
                            item.setUserEmail(user == null ? null : user.getEmail());
                            item.setUserFullName(user == null ? null : user.getFullName());
                            item.setUserAvatarUrl(user == null ? null : user.getAvatarUrl());
                            item.setAttachments(
                                    submissionAttachmentRepository.findByLessonSubmissionId(item.getLessonSubmissionId()));
                            var comments = submissionCommentRepository.findByLessonSubmissionIdOrderByCommentAtDesc(
                                    item.getLessonSubmissionId());
                            item.setLearnerNote(
                                    comments.stream()
                                            .filter(comment -> java.util.Objects.equals(comment.getUserId(), item.getUserId()))
                                            .map(SubmissionCommentModel::getContent)
                                            .findFirst()
                                            .orElse(null));
                            item.setEvaluatorComment(
                                    comments.stream()
                                            .filter(comment -> evaluatorUserIds.contains(comment.getUserId()))
                                            .map(SubmissionCommentModel::getContent)
                                            .findFirst()
                                            .orElse(null));
                            return item;
                        })
                .toList();
    }

    @Transactional
    public void commentSubmission(Long lessonSubmissionId, Long evaluatorUserId, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new BadRequestException("submission.comment.invalid", "Noi dung nhan xet khong duoc de trong");
        }

        lessonSubmissionRepository
                .findById(lessonSubmissionId)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        "lesson.submission.not.found", "Khong tim thay bai nop"));

        Long courseId =
                lessonSubmissionRepository
                        .findCourseIdByLessonSubmissionId(lessonSubmissionId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.not.found", "Khong tim thay khoa hoc cua bai nop"));

        if (courseEvaluatorAssignmentRepository.findEvaluatorUserIdsByCourseId(courseId).stream()
                .noneMatch(evaluatorUserId::equals)) {
            throw new ForbiddenException(
                    "evaluator.course.forbidden", "Ban khong duoc phan cong danh gia bai nop nay");
        }

        submissionCommentRepository.save(
                SubmissionCommentModel.builder()
                        .lessonSubmissionId(lessonSubmissionId)
                        .userId(evaluatorUserId)
                        .content(comment.trim())
                        .commentAt(System.currentTimeMillis())
                        .build());
    }

    @Transactional
    public void evaluateSubmission(
            Long lessonSubmissionId, Long evaluatorUserId, SubmissionEvaluationStatus evaluationStatus) {
        if (evaluationStatus == null) {
            throw new BadRequestException("submission.evaluation.invalid", "Trang thai danh gia khong hop le");
        }

        var submission =
                lessonSubmissionRepository
                        .findById(lessonSubmissionId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "lesson.submission.not.found", "Khong tim thay bai nop"));

        Long courseId =
                lessonSubmissionRepository
                        .findCourseIdByLessonSubmissionId(lessonSubmissionId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.not.found", "Khong tim thay khoa hoc cua bai nop"));

        if (courseEvaluatorAssignmentRepository.findEvaluatorUserIdsByCourseId(courseId).stream()
                .noneMatch(evaluatorUserId::equals)) {
            throw new ForbiddenException(
                    "evaluator.course.forbidden", "Ban khong duoc phan cong danh gia bai nop nay");
        }

        submission.setEvaluationStatus(evaluationStatus);
        lessonSubmissionRepository.save(submission);

        var nextLessonProgress =
                evaluationStatus == SubmissionEvaluationStatus.APPROVED
                        ? com.intern.hub.core.domain.model.enrollment.constant.LessonProgress.COMPLETED
                        : com.intern.hub.core.domain.model.enrollment.constant.LessonProgress.IN_PROGRESS;
        enrollmentProgressService.updateLessonProgressAndSyncCourse(
                submission.getLessonEnrollmentId(), nextLessonProgress);
    }
}
