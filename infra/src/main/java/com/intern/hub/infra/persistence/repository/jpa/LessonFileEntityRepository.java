package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import com.intern.hub.infra.persistence.entity.lesson.LessonFileEntity;
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

    @Query(
            """
                    SELECT SUM(f.fileSize)
                    FROM LessonFileEntity f
                    WHERE f.lessonEntity.lessonId = :lessonId
                    AND f.lessonFileType = :lessonType
                    """)
    Long sumFileSizeByLessonId(
            @Param("lessonId") Long lessonId, @Param("lessonType") LessonFileType lessonType);

    @Query(
            """
                    SELECT f
                    FROM LessonFileEntity f
                    WHERE f.lessonEntity.lessonId = :lessonId
                    """)
    List<LessonFileEntity> findAllByLessonId(@Param("lessonId") Long lessonId);
}
