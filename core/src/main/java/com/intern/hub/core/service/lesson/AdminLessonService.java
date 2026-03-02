package com.intern.hub.core.service.lesson;

import com.intern.hub.core.domain.model.lesson.LessonFileModel;
import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.core.repository.lesson.LessonFileRepository;
import com.intern.hub.core.repository.lesson.LessonRepository;
import com.intern.hub.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.List;
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

/**
 * Nhóm nghiệp vụ quản trị bài học (admin): tạo/cập nhật/xóa bài học, lấy danh sách/chi tiết và quản
 * lý ảnh + file bài học.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminLessonService {

  LessonRepository lessonRepository;
  LessonFileRepository lessonFileRepository;
  FileStorageRepository fileStorageRepository;
  StorageObjectLifecycleManager storageObjectLifecycleManager;
  LessonFileService lessonFileService;

  @NonFinal
  @Value("${aws.s3.bucket-url}")
  String bucketUrl;

  @NonFinal
  @Value("${aws.s3.paths.lesson}")
  String lessonPath;

  @NonFinal
  @Value("${aws.s3.max-file-size}")
  Long maxFileSize;

  @NonFinal
  @Value("${aws.s3.allow-types.image}")
  String allowTypesImage;

  /**
   * Tạo mới bài học. Có thể upload ảnh đại diện, file tài liệu (material) và file bài tập
   * (assignment).
   */
  @Transactional
  public void createLesson(
      LessonModel model,
      MultipartFile image,
      List<MultipartFile> lessonFiles,
      List<MultipartFile> assignmentFiles) {
    LessonModel saved = lessonRepository.save(model);
    Long lessonId = saved.getLessonId();

    if (image != null && !image.isEmpty()) {
      String imageUrl =
          fileStorageRepository.uploadFile(
              image, buildLessonImagePath(lessonId), maxFileSize, allowTypesImage);
      storageObjectLifecycleManager.cleanupOnRollback(imageUrl);
      saved.setLessonImageUrl(imageUrl);
      lessonRepository.save(saved);
    }

    if (hasItems(lessonFiles)) {
      lessonFileService.uploadFiles(lessonId, lessonFiles, LessonFileType.MATERIAL);
    }

    if (hasItems(assignmentFiles)) {
      lessonFileService.uploadFiles(lessonId, assignmentFiles, LessonFileType.ASSIGNMENT);
    }
  }

  /** Lấy danh sách bài học có phân trang cho màn admin. */
  @Transactional(readOnly = true)
  public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
    var res = lessonRepository.findAll(pageable);
    res.getContent().forEach(this::applyBucketUrl);
    return res;
  }

  /** Lấy chi tiết một bài học theo id. */
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

  /**
   * Cập nhật thông tin bài học. Hỗ trợ thay ảnh, thêm file mới và xóa các file hiện có theo danh
   * sách id.
   */
  @Transactional
  public void updateLesson(
      Long lessonId,
      LessonModel updateModel,
      MultipartFile newImage,
      List<MultipartFile> newLessonFiles,
      List<MultipartFile> newAssignmentFiles,
      List<Long> deleteFileIds) {
    LessonModel existing =
        lessonRepository
            .findById(lessonId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "lesson.not.found", "Không tìm thấy bài học id: " + lessonId));

    existing.setName(updateModel.getName());
    existing.setIntroduction(updateModel.getIntroduction());
    existing.setRequirements(updateModel.getRequirements());
    existing.setContent(updateModel.getContent());

    if (newImage != null && !newImage.isEmpty()) {
      String oldImageUrl = existing.getLessonImageUrl();
      String imageUrl =
          fileStorageRepository.uploadFile(
              newImage, buildLessonImagePath(lessonId), maxFileSize, allowTypesImage);
      storageObjectLifecycleManager.cleanupOnRollback(imageUrl);
      existing.setLessonImageUrl(imageUrl);

      if (hasText(oldImageUrl)) {
        storageObjectLifecycleManager.deleteAfterCommit(oldImageUrl);
      }
    }

    lessonRepository.save(existing);
    lessonRepository.flush();

    if (hasItems(deleteFileIds)) {
      deleteFileIds.forEach(lessonFileService::deleteFile);
    }

    if (hasItems(newLessonFiles)) {
      lessonFileService.uploadFiles(lessonId, newLessonFiles, LessonFileType.MATERIAL);
    }

    if (hasItems(newAssignmentFiles)) {
      lessonFileService.uploadFiles(lessonId, newAssignmentFiles, LessonFileType.ASSIGNMENT);
    }
  }

  /**
   * Xóa bài học và toàn bộ file liên quan. Các object trên storage sẽ được xóa sau khi transaction
   * commit.
   */
  @Transactional
  public void deleteLesson(Long lessonId) {
    LessonModel lessonModel =
        lessonRepository
            .findById(lessonId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "lesson.not.found", "Không tìm thấy bài học id: " + lessonId));

    List<LessonFileModel> lessonFileModels = lessonFileRepository.findAllByLessonId(lessonId);
    lessonFileModels.forEach(item -> lessonFileService.deleteFile(item.getLessonFileId()));

    if (hasText(lessonModel.getLessonImageUrl())) {
      storageObjectLifecycleManager.deleteAfterCommit(lessonModel.getLessonImageUrl());
    }

    lessonRepository.deleteById(lessonId);
  }

  // =========================== Utilities ===========================
  private static boolean hasItems(List<?> items) {
    return items != null && !items.isEmpty();
  }

  private static boolean hasText(String value) {
    return value != null && !value.isEmpty();
  }

  private String buildLessonImagePath(Long lessonId) {
    return lessonPath + lessonId + "/avatar";
  }

  private void applyBucketUrl(LessonModel model) {
    if (hasText(model.getLessonImageUrl())) {
      model.setLessonImageUrl(bucketUrl + model.getLessonImageUrl());
    }
  }
}
