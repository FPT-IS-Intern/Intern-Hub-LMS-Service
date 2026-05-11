package com.intern.hub.core.service.course;

import com.intern.hub.core.domain.model.enrollment.CourseEnrollmentModel;
import com.intern.hub.core.domain.model.enrollment.LessonEnrollmentModel;
import com.intern.hub.core.domain.model.enrollment.constant.CourseProgress;
import com.intern.hub.core.domain.model.enrollment.constant.LessonProgress;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.course.CourseRepository;
import com.intern.hub.core.repository.enrollment.CourseEnrollmentRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** Nghiệp vụ ghi danh khóa học và đồng bộ lesson enrollment cho user. */
public class CourseEnrollmentService {

  CourseRepository courseRepository;
  CourseLessonRepository courseLessonRepository;
  CourseEnrollmentRepository courseEnrollmentRepository;
  LessonEnrollmentRepository lessonEnrollmentRepository;

  @Transactional
  public void enrollCourse(Long courseId, Long userId) {
    courseRepository
        .findById(courseId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "course.not.found", "Không tìm thấy khóa học id: " + courseId));

    CourseEnrollmentModel courseEnrollment =
        courseEnrollmentRepository
            .findByCourseIdAndUserId(courseId, userId)
            .orElseGet(
                () ->
                    courseEnrollmentRepository.save(
                        CourseEnrollmentModel.builder()
                            .courseId(courseId)
                            .userId(userId)
                            .courseProgress(CourseProgress.IN_PROGRESS)
                            .build()));

    if (courseEnrollment.getCourseProgress() != CourseProgress.IN_PROGRESS) {
      courseEnrollment.setCourseProgress(CourseProgress.IN_PROGRESS);
      courseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
    }

    List<Long> lessonIds = courseLessonRepository.findLessonIdsByCourseId(courseId);
    if (lessonIds.isEmpty()) return;

    syncMissingLessonEnrollments(courseEnrollment.getCourseEnrollmentId(), lessonIds);
  }

  @Transactional
  public void syncCourseLessonEnrollments(Long courseId, List<Long> lessonIds) {
    if (lessonIds == null || lessonIds.isEmpty()) {
      return;
    }

    List<CourseEnrollmentModel> courseEnrollments =
        courseEnrollmentRepository.findAllByCourseId(courseId);
    for (CourseEnrollmentModel courseEnrollment : courseEnrollments) {
      syncMissingLessonEnrollments(courseEnrollment.getCourseEnrollmentId(), lessonIds);
    }
  }

  private void syncMissingLessonEnrollments(Long courseEnrollmentId, List<Long> lessonIds) {
    List<Long> enrolledLessonIds =
        lessonEnrollmentRepository.findLessonIdsByCourseEnrollmentId(courseEnrollmentId);

    Set<Long> missingLessonIds = new HashSet<>(lessonIds);
    enrolledLessonIds.forEach(missingLessonIds::remove);

    if (missingLessonIds.isEmpty()) {
      return;
    }

    List<LessonEnrollmentModel> lessonEnrollments = new ArrayList<>(missingLessonIds.size());
    for (Long lessonId : missingLessonIds) {
      lessonEnrollments.add(
          LessonEnrollmentModel.builder()
              .courseEnrollmentId(courseEnrollmentId)
              .lessonId(lessonId)
              .lessonProgress(LessonProgress.IN_PROGRESS)
              .build());
    }

    lessonEnrollmentRepository.saveAll(lessonEnrollments);
  }
}
