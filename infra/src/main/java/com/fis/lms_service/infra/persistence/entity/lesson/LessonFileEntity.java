package com.fis.lms_service.infra.persistence.entity.lesson;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Admin 1/26/2026
 */
@Entity
@Table(name = "lesson_files")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonFileEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "lesson_file_id", nullable = false, updatable = false)
    Long lessonFileId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    LessonEntity lessonEntity;

    @Column(name = "file_url", nullable = false, columnDefinition = "text")
    String fileUrl;

    @Column(name = "file_name", nullable = false, columnDefinition = "text")
    String fileName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lesson_file_type", nullable = false)
    LessonFileType lessonFileType;

    @Column(name = "file_size", nullable = false)
    Long fileSize;
}
