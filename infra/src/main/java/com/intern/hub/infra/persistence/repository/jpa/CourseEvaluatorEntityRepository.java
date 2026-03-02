package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.course.CourseEvaluatorEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CourseEvaluatorEntityRepository
        extends JpaRepository<@NonNull CourseEvaluatorEntity, @NonNull Long> {

    void deleteByCourseEntity_CourseId(Long courseId);
}
