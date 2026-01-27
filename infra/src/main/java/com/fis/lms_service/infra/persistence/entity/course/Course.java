package com.fis.lms_service.infra.persistence.entity.course;

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

/** Admin 1/27/2026 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends AuditEntity {

  @Id
  @SnowflakeGenerated
  @Column(name = "course_id", nullable = false, updatable = false)
  Long courseId;

  @Column(name = "name", nullable = false, length = 128)
  String name;

  @Column(name = "description", nullable = false, columnDefinition = "text")
  String description;

  @Column(name = "course_image_url", nullable = false, columnDefinition = "text")
  String courseImageUrl;
}
