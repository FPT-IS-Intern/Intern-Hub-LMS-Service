package com.fis.lms_service.core.service.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.repository.course.CourseLessonRepository;
import com.fis.lms_service.core.repository.enrollment.LessonEnrollmentRepository;
import com.fis.lms_service.core.repository.lesson.LessonRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonQueryService {

  LessonRepository lessonRepository;
  CourseLessonRepository courseLessonRepository;
  LessonEnrollmentRepository lessonEnrollmentRepository;

  @NonFinal
  @Value("${aws.s3.bucket-url}")
  String bucketUrl;

  @Transactional(readOnly = true)
  public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
    var res = lessonRepository.findAll(pageable);
    res.getContent().forEach(this::applyBucketUrl);
    return res;
  }

  @Transactional(readOnly = true)
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
    lessons.forEach(this::applyBucketUrl);
    return new PageImpl<>(lessons, pageable, total);
  }

  @Transactional(readOnly = true)
  public LessonModel getLesson(Long lessonId) {
    LessonModel model =
        lessonRepository
            .findById(lessonId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "lesson.not.found", "Không tìm thấy bài học id: " + lessonId));

    applyBucketUrl(model);
    return model;
  }

  @Transactional(readOnly = true)
  public Long getLessonEnrollmentId(Long lessonId, Long userId) {
    if (userId == null) {
      return null;
    }
    return lessonEnrollmentRepository
        .findLessonEnrollmentIdByLessonIdAndUserId(lessonId, userId)
        .orElse(null);
  }

  private void applyBucketUrl(LessonModel model) {
    if (hasText(model.getLessonImageUrl())) {
      model.setLessonImageUrl(bucketUrl + model.getLessonImageUrl());
    }
  }

  private static boolean hasText(String value) {
    return value != null && !value.isEmpty();
  }
}
