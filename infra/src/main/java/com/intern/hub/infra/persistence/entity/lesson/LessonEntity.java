package com.intern.hub.infra.persistence.entity.lesson;

import com.intern.hub.infra.generator.SnowflakeGenerated;
import com.intern.hub.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Admin 1/26/2026 */
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

  @Column(name = "name", nullable = false, columnDefinition = "text", length = 255)
  String name;

  @Column(name = "introduction", nullable = false, columnDefinition = "text", length = 255)
  String introduction;

  @Column(name = "content", nullable = false, columnDefinition = "text", length = 255)
  String content;

  @Column(name = "requirements", nullable = false, columnDefinition = "text", length = 255)
  String requirements;

  @Column(name = "lesson_image_url", nullable = true, columnDefinition = "text")
  String lessonImageUrl;
}
