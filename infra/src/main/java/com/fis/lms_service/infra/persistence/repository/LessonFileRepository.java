package com.fis.lms_service.infra.persistence.repository;

import com.fis.lms_service.infra.persistence.entity.lesson.LessonFile;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Admin 1/26/2026 */
@Repository
public interface LessonFileRepository extends JpaRepository<@NonNull LessonFile, @NonNull Long> {}
