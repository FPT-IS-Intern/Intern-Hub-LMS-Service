package com.fis.lms_service.core.service.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.core.repository.lesson.LessonRepository;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 1/29/2026
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {

    LessonRepository lessonRepository;
    LessonFileRepository lessonFileRepository;
    FileStorageRepository fileStorageRepository;

    LessonFileService lessonFileService;

    @NonFinal
    @Value("${aws.s3.paths.lesson}")
    String lessonPath;

    @NonFinal
    @Value("${aws.s3.max-file-size}")
    Long maxFileSize;

    @NonFinal
    @Value("${aws.s3.allow-types.image}")
    String allowTypesImage;

    @Transactional
    public void createLesson(LessonModel model, MultipartFile image, List<MultipartFile> files) {
        LessonModel saved = lessonRepository.save(model);
        Long lessonId = saved.getLessonId();

        if (image != null && !image.isEmpty()) {

            if (image.getSize() > maxFileSize) throw new RuntimeException();

            String imageUrl = fileStorageRepository.uploadFile(
                    image,
                    lessonPath + lessonId + "/avatar",
                    maxFileSize,
                    allowTypesImage
            );

            saved.setLessonImageUrl(imageUrl);
            lessonRepository.save(saved);
        }

        if (files != null && !files.isEmpty()) {
            lessonFileService.uploadFiles(lessonId, files);
        }
    }

    @Transactional
    public void deleteLesson(Long lessonId) {

        LessonModel lessonModel = lessonRepository.findById(lessonId).orElse(null);

        if (lessonModel == null) throw new RuntimeException();

        List<LessonFileModel> lessonFileModels = lessonFileRepository.findAllByLessonId(lessonId);

        lessonFileModels.forEach(
                lessonFileModel -> lessonFileService.deleteFile(lessonFileModel.getLessonFileId()));

        if (lessonModel.getLessonImageUrl() != null && !lessonModel.getLessonImageUrl().isEmpty()) {
            fileStorageRepository.deleteFile(lessonModel.getLessonImageUrl());
        }

        lessonRepository.deleteById(lessonId);
    }
}
