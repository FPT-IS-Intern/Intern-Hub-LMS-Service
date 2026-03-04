package com.intern.hub.core.service.enrollment;

import com.intern.hub.core.service.course.CourseEnrollmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentService {

    CourseEnrollmentService courseEnrollmentService;

    @Transactional
    public void enrollCourse(Long courseId, Long userId) {
        courseEnrollmentService.enrollCourse(courseId, userId);
    }
}
