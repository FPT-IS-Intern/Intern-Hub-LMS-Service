package com.intern.hub.core.service.lesson;

import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.enrollment.LessonEnrollmentRepository;
import com.intern.hub.core.repository.lesson.LessonRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.List;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** Nghiệp vụ tra cứu bài học cho user/admin. */
public class LessonQueryService {

  LessonRepository lessonRepository;
  CourseLessonRepository courseLessonRepository;
  LessonEnrollmentRepository lessonEnrollmentRepository;

  @NonFinal
  @Value("${aws.s3.bucket-url}")
  String bucketUrl;

  @Transactional(readOnly = true)
  /** Lấy toàn bộ bài học có phân trang. */
  public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
    var res = lessonRepository.findAll(pageable);
    res.getContent().forEach(this::applyBucketUrl);
    return res;
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
    lessons.forEach(this::applyBucketUrl);
    return new PageImpl<>(lessons, pageable, total);
  }

  @Transactional(readOnly = true)
  /** Lấy chi tiết bài học theo id. */
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
  /** Tìm lessonEnrollmentId theo lessonId + userId (trả null nếu không có). */
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
