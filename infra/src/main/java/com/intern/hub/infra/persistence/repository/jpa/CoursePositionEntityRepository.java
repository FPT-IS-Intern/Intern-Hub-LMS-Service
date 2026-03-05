package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.course.CoursePositionEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CoursePositionEntityRepository
        extends JpaRepository<@NonNull CoursePositionEntity, @NonNull Long> {

    void deleteByCourseEntity_CourseId(Long courseId);

    @Query("""
            SELECT cp.positionId
            FROM CoursePositionEntity cp
            WHERE cp.courseEntity.courseId = :courseId
            ORDER BY cp.positionId
            """)
    List<Long> findPositionIdsByCourseId(@Param("courseId") Long courseId);
}
