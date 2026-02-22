package com.fis.lms_service.core.service.course;

import com.fis.lms_service.core.domain.model.course.CourseModel;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.course.CourseRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

  CourseRepository courseRepository;
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
  public void createCourse(CourseModel model, MultipartFile image) {
    if (image == null || image.isEmpty()) {
      throw new BadRequestException("course.image.required", "Ảnh khóa học là bắt buộc");
    }

    CourseModel saved = courseRepository.save(model);
    Long courseId = saved.getCourseId();

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
}
