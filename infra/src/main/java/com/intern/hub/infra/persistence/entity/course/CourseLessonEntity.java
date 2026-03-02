package com.intern.hub.infra.persistence.entity.course;

import com.intern.hub.infra.generator.SnowflakeGenerated;
import com.intern.hub.infra.persistence.entity.base.AuditEntity;
import com.intern.hub.infra.persistence.entity.lesson.LessonEntity;
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
        name = "course_lessons",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "lesson_id"})})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseLessonEntity extends AuditEntity {

    @Id
    @SnowflakeGenerated
    @Column(name = "course_lesson_id", nullable = false, updatable = false)
    Long courseLessonId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    CourseEntity courseEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    LessonEntity lessonEntity;

    @Column(name = "order_index", nullable = false)
    Integer orderIndex;
}
