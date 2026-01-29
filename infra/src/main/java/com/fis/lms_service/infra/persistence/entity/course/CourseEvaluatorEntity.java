package com.fis.lms_service.infra.persistence.entity.course;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Admin 1/27/2026 */
@Entity
@Table(
    name = "course_evaluators",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "user_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseEvaluatorEntity extends AuditEntity {

  @Id
  @SnowflakeGenerated
  @Column(name = "course_evaluator_id", nullable = false, updatable = false)
  Long courseEvaluatorId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "course_id", nullable = false)
  CourseEntity courseEntity;

  @Column(name = "user_id", nullable = false)
  Long userId;
}
