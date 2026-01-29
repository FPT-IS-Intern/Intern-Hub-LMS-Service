package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 1/26/2026
 */
@Repository
public interface LessonFileEntityRepository
        extends JpaRepository<@NonNull LessonFileEntity, @NonNull Long> {

    @Query("SELECT SUM(f.fileSize) FROM LessonFileEntity f WHERE f.lessonEntity.lessonId = :lessonId")
    Long sumFileSizeByLessonId(@Param("lessonId") Long lessonId);

    List<LessonFileEntity> findAllByLessonEntity_LessonId(Long lessonEntityLessonId);
    
}
