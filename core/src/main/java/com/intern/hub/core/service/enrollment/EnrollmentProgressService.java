package com.intern.hub.core.service.enrollment;

import com.intern.hub.core.domain.model.enrollment.constant.CourseProgress;
import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentProgressService {

    LessonEnrollmentRepository lessonEnrollmentRepository;
    CourseEnrollmentRepository courseEnrollmentRepository;

    @Transactional
    public void updateLessonProgressAndSyncCourse(Long lessonEnrollmentId, LessonProgress lessonProgress) {
        lessonEnrollmentRepository.updateProgress(lessonEnrollmentId, lessonProgress);

        Long courseEnrollmentId =
                lessonEnrollmentRepository
                        .findCourseEnrollmentIdByLessonEnrollmentId(lessonEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.enrollment.not.found",
                                                "Khong tim thay course enrollment cua lesson enrollment"));

        syncCourseProgress(courseEnrollmentId);
    }

    @Transactional
    public void syncCourseProgress(Long courseEnrollmentId) {
        var courseEnrollment =
                courseEnrollmentRepository
                        .findById(courseEnrollmentId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.enrollment.not.found", "Khong tim thay course enrollment"));

        long totalLessons = lessonEnrollmentRepository.countByCourseEnrollmentId(courseEnrollmentId);
        long completedLessons =
                lessonEnrollmentRepository.countByCourseEnrollmentIdAndProgress(
                        courseEnrollmentId, LessonProgress.COMPLETED);

        CourseProgress nextProgress =
                totalLessons > 0 && totalLessons == completedLessons
                        ? CourseProgress.COMPLETED
                        : CourseProgress.IN_PROGRESS;

        if (courseEnrollment.getCourseProgress() != nextProgress) {
            courseEnrollment.setCourseProgress(nextProgress);
            courseEnrollmentRepository.save(courseEnrollment);
        }
    }
}
