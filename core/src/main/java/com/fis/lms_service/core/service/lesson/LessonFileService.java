package com.fis.lms_service.core.service.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
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
public class LessonFileService {

  LessonFileRepository lessonFileRepository;
  FileStorageRepository fileStorageRepository;

  @NonFinal
  @Value("${aws.s3.paths.lesson}")
  String lessonPath;

  @NonFinal
  @Value("${aws.s3.max-file-size}")
  Long maxFileSize;

  @Transactional
  public void uploadFiles(Long lessonId, List<MultipartFile> files) {

    long currentTotalSize = lessonFileRepository.getTotalSizeByLessonId(lessonId);

    long uploadSize = files.stream().mapToLong(MultipartFile::getSize).sum();

    if (currentTotalSize + uploadSize > maxFileSize) throw new RuntimeException();

    for (MultipartFile file : files) {
      String s3Key = fileStorageRepository.uploadFile(file, lessonPath + lessonId);

      LessonFileModel model =
          LessonFileModel.builder()
              .lessonId(lessonId)
              .fileUrl(s3Key)
              .fileName(file.getOriginalFilename())
              .fileSize(file.getSize())
              .build();
      lessonFileRepository.save(model);
    }
  }

  public List<LessonFileModel> getFiles(Long lessonId) {
    return lessonFileRepository.findAllByLessonId(lessonId).stream()
        .peek(m -> m.setFileUrl(fileStorageRepository.getPrivateUrl(m.getFileUrl())))
        .toList();
  }

  @Transactional
  public void deleteFile(Long lessonFileId) {

    LessonFileModel lessonFileModel = lessonFileRepository.findByLessonFileId(lessonFileId);

    lessonFileRepository.deleteById(lessonFileModel.getLessonFileId());
    fileStorageRepository.deleteFile(lessonFileModel.getFileUrl());
  }
}
