package com.fis.lms_service.infra.persistence.entity.course;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Admin 1/27/2026
 */
@Entity
@Table(
        name = "course_positions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "position_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoursePositionEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "course_position_id", nullable = false, updatable = false)
    Long coursePositionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    CourseEntity courseEntity;

    @Column(name = "position_id", nullable = false)
    Long positionId;
}
