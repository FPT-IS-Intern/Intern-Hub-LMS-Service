package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;

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
public interface LessonFileEntityRepository
        extends JpaRepository<@NonNull LessonFileEntity, @NonNull Long> {

    @Query("SELECT SUM(f.fileSize) FROM LessonFileEntity f " +
            "WHERE f.lessonEntity.lessonId = :lessonId " +
            "AND f.lessonFileType = :lessonType")
    Long getTotalSizeByLessonId(
            @Param("lessonId") Long lessonId,
            @Param("lessonType") LessonFileType lessonType
    );

    List<LessonFileEntity> findAllByLessonEntity_LessonId(Long lessonEntityLessonId);
}
