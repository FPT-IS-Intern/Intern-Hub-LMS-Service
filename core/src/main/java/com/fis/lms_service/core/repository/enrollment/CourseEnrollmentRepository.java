package com.fis.lms_service.core.repository.enrollment;

import com.fis.lms_service.core.domain.model.enrollment.CourseEnrollmentModel;

import java.util.Optional;

public interface CourseEnrollmentRepository {

    Optional<CourseEnrollmentModel> findByCourseIdAndUserId(Long courseId, Long userId);

    Optional<CourseEnrollmentModel> findById(Long courseEnrollmentId);

    CourseEnrollmentModel save(CourseEnrollmentModel model);
}
