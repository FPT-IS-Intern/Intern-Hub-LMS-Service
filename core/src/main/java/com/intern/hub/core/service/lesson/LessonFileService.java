package com.intern.hub.core.service.lesson;

import com.intern.hub.core.domain.model.lesson.LessonFileModel;
import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.core.repository.lesson.LessonFileRepository;
import com.intern.hub.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Admin 1/29/2026 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** Nghiệp vụ quản lý file của bài học (material/assignment). */
public class LessonFileService {

  LessonFileRepository lessonFileRepository;
  FileStorageRepository fileStorageRepository;
  StorageObjectLifecycleManager storageObjectLifecycleManager;

  @NonFinal
  @Value("${aws.s3.paths.lesson}")
  String lessonPath;

  @NonFinal
  @Value("${aws.s3.max-total-size}")
  Long maxTotalSize;

  @NonFinal
  @Value("${aws.s3.allow-types.document}")
  String allowTypesDocument;

  @Transactional
  /** Upload danh sách file cho bài học, kiểm tra tổng dung lượng không vượt quá giới hạn. */
  public void uploadFiles(
      Long lessonId, List<MultipartFile> files, LessonFileType lessonFileType, Long actorId) {
    if (files == null || files.isEmpty()) {
      return;
    }

    long currentTotalSize = lessonFileRepository.getTotalSizeByLessonId(lessonId, lessonFileType);

    long uploadSize = files.stream().mapToLong(MultipartFile::getSize).sum();

    if (currentTotalSize + uploadSize > maxTotalSize)
      throw new BadRequestException(
          "file.size.exceeded", "Tổng dung lượng file vượt quá giới hạn cho phép");

    for (MultipartFile file : files) {
      String s3Key =
          fileStorageRepository.uploadFile(
              file, lessonPath + lessonId, actorId, maxTotalSize, allowTypesDocument);
      storageObjectLifecycleManager.cleanupOnRollback(s3Key, actorId);

      LessonFileModel model =
          LessonFileModel.builder()
              .lessonId(lessonId)
              .fileUrl(s3Key)
              .fileName(file.getOriginalFilename())
              .fileSize(file.getSize())
              .lessonFileType(lessonFileType)
              .build();
      lessonFileRepository.save(model);
    }
  }

  /** Lấy danh sách file của bài học và gắn URL tải tạm thời. */
  public List<LessonFileModel> getFiles(Long lessonId) {
    return lessonFileRepository.findAllByLessonId(lessonId).stream()
        .peek(m -> m.setFileUrl(fileStorageRepository.getPrivateUrl(m.getFileUrl())))
        .toList();
  }

  @Transactional
  /** Xóa file bài học theo id (xóa trên storage và trong DB). */
  public void deleteFile(Long lessonFileId, Long actorId) {

    LessonFileModel lessonFileModel =
        lessonFileRepository
            .findById(lessonFileId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "lesson.file.not.found",
                        "Không tìm thấy file bài học id: " + lessonFileId));

    lessonFileRepository.deleteById(lessonFileModel.getLessonFileId());
    storageObjectLifecycleManager.deleteAfterCommit(lessonFileModel.getFileUrl(), actorId);
  }
}
