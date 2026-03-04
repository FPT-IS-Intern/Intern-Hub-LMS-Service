package com.intern.hub.core.service.course;

import com.intern.hub.core.domain.model.course.CourseModel;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.course.CourseRepository;
import com.intern.hub.core.repository.lesson.LessonRepository;
import com.intern.hub.core.service.lesson.LessonQueryService;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    CourseRepository courseRepository;
    CourseLessonRepository courseLessonRepository;
    LessonRepository lessonRepository;
    LessonQueryService lessonQueryService;

    @Transactional(readOnly = true)
    public Page<@NonNull CourseModel> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public CourseModel getCourse(Long courseId) {
        return courseRepository
                .findById(courseId)
                .orElseThrow(
                        () -> new NotFoundException("course.not.found", "Không tìm thấy khóa học id: " + courseId));
    }

    @Transactional(readOnly = true)
    public List<Long> getCourseLessonIds(Long courseId) {
        courseRepository
                .findById(courseId)
                .orElseThrow(
                        () -> new NotFoundException("course.not.found", "Không tìm thấy khóa học id: " + courseId));
        return courseLessonRepository.findLessonIdsByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<LessonModel> getCourseLessons(Long courseId) {
        List<Long> lessonIds = getCourseLessonIds(courseId);
        if (lessonIds.isEmpty()) {
            return List.of();
        }
        return lessonRepository.findAllByIds(lessonIds);
    }

    @Transactional(readOnly = true)
    public Page<@NonNull LessonModel> getCourseLessons(Long courseId, Pageable pageable) {
        courseRepository
                .findById(courseId)
                .orElseThrow(
                        () -> new NotFoundException("course.not.found", "Không tìm thấy khóa học id: " + courseId));
        return lessonQueryService.getLessonsByCourse(courseId, pageable);
    }
}
