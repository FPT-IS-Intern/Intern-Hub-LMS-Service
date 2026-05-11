package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.lesson.LessonEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Admin 1/26/2026 */
@Repository
public interface LessonEntityRepository
    extends JpaRepository<@NonNull LessonEntity, @NonNull Long> {}
