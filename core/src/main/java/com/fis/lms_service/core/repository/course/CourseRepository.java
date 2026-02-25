package com.fis.lms_service.core.repository.course;

import com.fis.lms_service.core.domain.model.course.CourseModel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CourseRepository {

    CourseModel save(CourseModel courseModel);

    Optional<CourseModel> findById(Long courseId);

    void deleteById(Long courseId);

    void deleteWithRelationsById(Long courseId);

    Page<@NonNull CourseModel> findAll(Pageable pageable);
}
