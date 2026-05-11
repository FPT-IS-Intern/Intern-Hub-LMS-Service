package com.intern.hub.core.service.lesson;

import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.core.repository.lesson.LessonRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** Nghiệp vụ tra cứu bài học cho user/admin. */
public class LessonQueryService {

  LessonRepository lessonRepository;
  CourseLessonRepository courseLessonRepository;
  CourseEnrollmentRepository courseEnrollmentRepository;
  LessonEnrollmentRepository lessonEnrollmentRepository;

  @Transactional(readOnly = true)
  /** Lấy toàn bộ bài học có phân trang. */
  public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
    return lessonRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  /** Lấy danh sách bài học của một course theo phân trang. */
  public Page<@NonNull LessonModel> getLessonsByCourse(Long courseId, Pageable pageable) {
    List<Long> lessonIds = courseLessonRepository.findLessonIdsByCourseId(courseId);
    int total = lessonIds.size();
    int offset = (int) pageable.getOffset();
    if (offset >= total) {
      return new PageImpl<>(List.of(), pageable, total);
    }
    int end = Math.min(offset + pageable.getPageSize(), total);
    List<Long> pageIds = lessonIds.subList(offset, end);

    List<LessonModel> lessons = lessonRepository.findAllByIds(pageIds);
    return new PageImpl<>(lessons, pageable, total);
  }

  @Transactional(readOnly = true)
  /** Lấy chi tiết bài học theo id. */
  public LessonModel getLesson(Long lessonId) {
    return lessonRepository
        .findById(lessonId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "lesson.not.found", "Không tìm thấy bài học id: " + lessonId));
  }

  @Transactional(readOnly = true)
  public Optional<com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel>
      getLessonEnrollment(Long lessonId, Long userId) {
    if (userId == null) {
      return Optional.empty();
    }
    List<com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel> enrollments =
        lessonEnrollmentRepository.findAllByLessonIdAndUserId(lessonId, userId);
    if (enrollments.size() != 1) {
      return Optional.empty();
    }
    return Optional.of(enrollments.get(0));
  }

  @Transactional(readOnly = true)
  public Optional<com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel>
      getLessonEnrollment(Long courseId, Long lessonId, Long userId) {
    if (userId == null) {
      return Optional.empty();
    }
    return courseEnrollmentRepository
        .findByCourseIdAndUserId(courseId, userId)
        .flatMap(
            courseEnrollment ->
                lessonEnrollmentRepository.findByCourseEnrollmentIdAndLessonId(
                    courseEnrollment.getCourseEnrollmentId(), lessonId));
  }

  @Transactional(readOnly = true)
  /** Tìm lessonEnrollmentId theo lessonId + userId khi chỉ còn đúng 1 ngữ cảnh course. */
  public Long getLessonEnrollmentId(Long lessonId, Long userId) {
    return getLessonEnrollment(lessonId, userId)
        .map(
            com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel
                ::getLessonEnrollmentId)
        .orElse(null);
  }
}
