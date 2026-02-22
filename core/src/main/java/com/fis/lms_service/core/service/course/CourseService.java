package com.fis.lms_service.core.service.course;

import com.fis.lms_service.core.domain.model.course.CourseModel;
import com.fis.lms_service.core.domain.model.enrollment.CourseEnrollmentModel;
import com.fis.lms_service.core.domain.model.enrollment.LessonEnrollmentModel;
import com.fis.lms_service.core.domain.model.enrollment.constant.CourseProgress;
import com.fis.lms_service.core.domain.model.enrollment.constant.LessonProgress;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.course.CourseLessonRepository;
import com.fis.lms_service.core.repository.course.CourseRepository;
import com.fis.lms_service.core.repository.enrollment.CourseEnrollmentRepository;
import com.fis.lms_service.core.repository.enrollment.LessonEnrollmentRepository;
import com.fis.lms_service.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

  CourseRepository courseRepository;
  CourseLessonRepository courseLessonRepository;
  CourseEnrollmentRepository courseEnrollmentRepository;
  LessonEnrollmentRepository lessonEnrollmentRepository;
  FileStorageRepository fileStorageRepository;
  StorageObjectLifecycleManager storageObjectLifecycleManager;

  @NonFinal
  @Value("${aws.s3.bucket-url}")
  String bucketUrl;

  @NonFinal
  @Value("${aws.s3.paths.course}")
  String coursePath;

  @NonFinal
  @Value("${aws.s3.max-file-size}")
  Long maxFileSize;

  @NonFinal
  @Value("${aws.s3.allow-types.image}")
  String allowTypesImage;

  @Transactional
  public void createCourse(CourseModel model, MultipartFile image, List<Long> lessonIds) {
    if (image == null || image.isEmpty()) {
      throw new BadRequestException("course.image.required", "Ảnh khóa học là bắt buộc");
    }

    CourseModel saved = courseRepository.save(model);
    Long courseId = saved.getCourseId();

    if (hasItems(lessonIds)) {
      courseLessonRepository.saveCourseLessons(courseId, distinctOrdered(lessonIds));
    }

    String imageUrl =
        fileStorageRepository.uploadFile(
            image, buildCourseImagePath(courseId), maxFileSize, allowTypesImage);
    storageObjectLifecycleManager.cleanupOnRollback(imageUrl);

    saved.setCourseImageUrl(imageUrl);
    courseRepository.save(saved);
  }

  @Transactional(readOnly = true)
  public Page<@NonNull CourseModel> getCourses(Pageable pageable) {
    Page<@NonNull CourseModel> page = courseRepository.findAll(pageable);
    page.getContent().forEach(this::applyBucketUrl);
    return page;
  }

  @Transactional(readOnly = true)
  public CourseModel getCourse(Long courseId) {
    CourseModel model =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "course.not.found", "Không tìm thấy khóa học id: " + courseId));
    applyBucketUrl(model);
    return model;
  }

  @Transactional
  public void updateCourse(Long courseId, CourseModel updateModel, MultipartFile newImage) {
    CourseModel existing =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "course.not.found", "Không tìm thấy khóa học id: " + courseId));

    existing.setName(updateModel.getName());
    existing.setDescription(updateModel.getDescription());

    if (newImage != null && !newImage.isEmpty()) {
      String oldImageUrl = existing.getCourseImageUrl();
      String newImageUrl =
          fileStorageRepository.uploadFile(
              newImage, buildCourseImagePath(courseId), maxFileSize, allowTypesImage);
      storageObjectLifecycleManager.cleanupOnRollback(newImageUrl);

      existing.setCourseImageUrl(newImageUrl);
      if (hasText(oldImageUrl)) {
        storageObjectLifecycleManager.deleteAfterCommit(oldImageUrl);
      }
    }

    courseRepository.save(existing);
  }

  @Transactional
  public void deleteCourse(Long courseId) {
    CourseModel courseModel =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "course.not.found", "Không tìm thấy khóa học id: " + courseId));

    courseRepository.deleteWithRelationsById(courseId);
    if (hasText(courseModel.getCourseImageUrl())) {
      storageObjectLifecycleManager.deleteAfterCommit(courseModel.getCourseImageUrl());
    }
  }

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
    if (lessonIds.isEmpty()) {
      return;
    }

    List<Long> enrolledLessonIds =
        lessonEnrollmentRepository.findLessonIdsByCourseEnrollmentId(
            courseEnrollment.getCourseEnrollmentId());

    Set<Long> missingLessonIds = new HashSet<>(lessonIds);
    missingLessonIds.removeAll(enrolledLessonIds);

    if (missingLessonIds.isEmpty()) {
      return;
    }

    List<LessonEnrollmentModel> lessonEnrollments = new ArrayList<>(missingLessonIds.size());
    for (Long lessonId : missingLessonIds) {
      lessonEnrollments.add(
          LessonEnrollmentModel.builder()
              .courseEnrollmentId(courseEnrollment.getCourseEnrollmentId())
              .lessonId(lessonId)
              .lessonProgress(LessonProgress.IN_PROGRESS)
              .build());
    }

    lessonEnrollmentRepository.saveAll(lessonEnrollments);
  }

  private void applyBucketUrl(CourseModel model) {
    if (hasText(model.getCourseImageUrl())) {
      model.setCourseImageUrl(bucketUrl + model.getCourseImageUrl());
    }
  }

  private String buildCourseImagePath(Long courseId) {
    return coursePath + courseId + "/avatar";
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static boolean hasItems(List<?> items) {
    return items != null && !items.isEmpty();
  }

  private static List<Long> distinctOrdered(List<Long> lessonIds) {
    Set<Long> unique = new java.util.LinkedHashSet<>();
    for (Long lessonId : lessonIds) {
      if (lessonId != null) {
        unique.add(lessonId);
      }
    }
    return new ArrayList<>(unique);
  }
}
