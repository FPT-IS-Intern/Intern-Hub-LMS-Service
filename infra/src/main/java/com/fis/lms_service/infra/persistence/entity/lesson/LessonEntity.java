package com.fis.lms_service.infra.persistence.entity.lesson;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Admin 1/26/2026
 */
@Entity
@Table(name = "lessons")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "lesson_id", nullable = false, updatable = false)
    Long lessonId;

    @Column(name = "name", nullable = false, columnDefinition = "text")
    String name;

    @Column(name = "introduction", nullable = false, columnDefinition = "text")
    String introduction;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    String content;

    @Column(name = "requirements", nullable = false, columnDefinition = "text")
    String requirements;

    @Column(name = "lesson_image_url", nullable = false, columnDefinition = "text")
    String lessonImageUrl;
}
