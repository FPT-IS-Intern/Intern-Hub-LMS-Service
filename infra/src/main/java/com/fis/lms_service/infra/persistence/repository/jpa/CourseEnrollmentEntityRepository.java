package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.infra.persistence.entity.enrollment.CourseEnrollmentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CourseEnrollmentEntityRepository
        extends JpaRepository<@NonNull CourseEnrollmentEntity, @NonNull Long> {
}
