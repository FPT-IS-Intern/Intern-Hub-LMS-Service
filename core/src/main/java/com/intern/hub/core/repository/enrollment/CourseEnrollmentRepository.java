package com.intern.hub.core.repository.enrollment;

import com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository {

    Optional<CourseEnrollmentModel> findByCourseIdAndUserId(Long courseId, Long userId);

    List<CourseEnrollmentModel> findAllByCourseId(Long courseId);

    Optional<CourseEnrollmentModel> findById(Long courseEnrollmentId);

    CourseEnrollmentModel save(CourseEnrollmentModel model);
}
