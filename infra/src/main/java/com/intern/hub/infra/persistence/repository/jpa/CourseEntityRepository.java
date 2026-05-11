package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.course.CourseEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Admin 1/26/2026 */
@Repository
public interface CourseEntityRepository
    extends JpaRepository<@NonNull CourseEntity, @NonNull Long> {}
