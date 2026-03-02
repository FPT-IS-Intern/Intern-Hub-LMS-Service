package com.intern.hub.infra.persistence.entity.enrollment;

import com.intern.hub.core.domain.model.enrollment.constant.CourseProgress;
import com.intern.hub.infra.generator.SnowflakeGenerated;
import com.intern.hub.infra.persistence.entity.base.AuditEntity;
import com.intern.hub.infra.persistence.entity.course.CourseEntity;
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
        name = "course_enrollments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "user_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseEnrollmentEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "course_enrollment_id", nullable = false, updatable = false)
    Long courseEnrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    CourseEntity courseEntity;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_progress", nullable = false)
    CourseProgress courseProgress;
}
