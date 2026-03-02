package com.intern.hub.core.repository.enrollment;

import com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel;
import java.util.Optional;

public interface CourseEnrollmentRepository {

  Optional<CourseEnrollmentModel> findByCourseIdAndUserId(Long courseId, Long userId);

  Optional<CourseEnrollmentModel> findById(Long courseEnrollmentId);

  CourseEnrollmentModel save(CourseEnrollmentModel model);
}
