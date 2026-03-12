package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.enrollment.CourseEnrollmentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CourseEnrollmentEntityRepository
        extends JpaRepository<@NonNull CourseEnrollmentEntity, @NonNull Long> {

    void deleteByCourseEntity_CourseId(Long courseId);

    Optional<CourseEnrollmentEntity> findByCourseEntity_CourseIdAndUserId(Long courseId, Long userId);

    List<CourseEnrollmentEntity> findAllByCourseEntity_CourseIdAndUserIdOrderByCreatedAtDesc(
            Long courseId, Long userId);
}
