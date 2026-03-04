package com.intern.hub.core.service.enrollment;

import com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel;
import com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.core.service.course.CourseEnrollmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentService {

    CourseEnrollmentService courseEnrollmentService;
    CourseEnrollmentRepository courseEnrollmentRepository;
    LessonEnrollmentRepository lessonEnrollmentRepository;

    @Transactional
    public void enrollCourse(Long courseId, Long userId) {
        courseEnrollmentService.enrollCourse(courseId, userId);
    }

    @Transactional(readOnly = true)
    public Optional<CourseEnrollmentModel> getCourseEnrollment(Long courseId, Long userId) {
        return courseEnrollmentRepository.findByCourseIdAndUserId(courseId, userId);
    }

    @Transactional(readOnly = true)
    public Optional<LessonEnrollmentModel> getLessonEnrollment(Long lessonId, Long userId) {
        return lessonEnrollmentRepository.findByLessonIdAndUserId(lessonId, userId);
    }
}
