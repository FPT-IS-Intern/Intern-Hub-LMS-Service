package com.fis.lms_service.infra.persistence.entity.enrollment;

import com.fis.lms_service.infra.generator.SnowflakeGenerated;
import com.fis.lms_service.infra.persistence.entity.base.AuditEntity;
import com.fis.lms_service.infra.persistence.entity.enrollment.constant.LessonProgress;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Admin 1/27/2026
 */
@Entity
@Table(
        name = "lesson_enrollments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_enrollment_id", "lesson_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonEnrollmentEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "lesson_enrollment_id", nullable = false, updatable = false)
    Long lessonEnrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_enrollment_id", nullable = false)
    CourseEnrollmentEntity courseEnrollmentEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    LessonEntity lessonEntity;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lesson_progress", nullable = false)
    LessonProgress lessonProgress;
}
