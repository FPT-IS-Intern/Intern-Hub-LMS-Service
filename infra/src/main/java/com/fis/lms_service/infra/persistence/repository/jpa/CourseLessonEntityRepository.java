package com.fis.lms_service.infra.persistence.repository.jpa;

import com.fis.lms_service.infra.persistence.entity.course.CourseLessonEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 1/26/2026
 */
@Repository
public interface CourseLessonEntityRepository
        extends JpaRepository<@NonNull CourseLessonEntity, @NonNull Long> {

    void deleteByCourseEntity_CourseId(Long courseId);

    @Query(
            "select cl.lessonEntity.lessonId from CourseLessonEntity cl "
                    + "where cl.courseEntity.courseId = :courseId "
                    + "order by cl.orderIndex asc")
    List<Long> findLessonIdsByCourseId(Long courseId);
}
