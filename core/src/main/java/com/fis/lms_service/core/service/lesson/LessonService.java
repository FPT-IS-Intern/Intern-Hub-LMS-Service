package com.fis.lms_service.core.service.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.core.repository.lesson.LessonRepository;

import java.util.List;

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


    @Transactional
    public void createLesson(
            LessonModel model,
            MultipartFile image,
            List<MultipartFile> lessonFiles,
            List<MultipartFile> assignmentFiles) {
        LessonModel saved = lessonRepository.save(model);

        Long lessonId = saved.getLessonId();

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageRepository.uploadFile(
                    image, lessonPath + lessonId + "/avatar", maxFileSize, allowTypesImage
            );
            saved.setLessonImageUrl(imageUrl);

            lessonRepository.save(saved);
        }

        if (lessonFiles != null && !lessonFiles.isEmpty()) {
            lessonFileService.uploadFiles(lessonId, lessonFiles, LessonFileType.MATERIAL);
        }

        if (assignmentFiles != null && !assignmentFiles.isEmpty()) {
            lessonFileService.uploadFiles(lessonId, assignmentFiles, LessonFileType.ASSIGNMENT);
        }
    }

    @Transactional(readOnly = true)
    public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
        var res = lessonRepository.findAll(pageable);

        res.getContent().forEach(x -> {
            if (x.getLessonImageUrl() != null && !x.getLessonImageUrl().isEmpty())
                x.setLessonImageUrl(bucketUrl + x.getLessonImageUrl());
        });
        return res;
    }

    @Transactional(readOnly = true)
    public LessonModel getLesson(Long lessonId) {
        LessonModel model = lessonRepository
                .findById(lessonId)
                .orElseThrow(() -> new NotFoundException("lesson.not.found", "Không tìm thấy bài học"));

        if (model.getLessonImageUrl() != null && !model.getLessonImageUrl().isEmpty()) {
            model.setLessonImageUrl(bucketUrl + model.getLessonImageUrl());
        }

        return model;
    }

    @Transactional
    public void updateLesson(
            Long lessonId,
            LessonModel updateModel,
            MultipartFile newImage,
            List<MultipartFile> newLessonFiles,
            List<MultipartFile> newAssignmentFiles,
            List<Long> deleteFileIds) {

        LessonModel existing = lessonRepository
                .findById(lessonId)
                .orElseThrow(() -> new NotFoundException("lesson.not.found", "Không tìm thấy bài học"));

        existing.setName(updateModel.getName());
        existing.setIntroduction(updateModel.getIntroduction());
        existing.setRequirements(updateModel.getRequirements());
        existing.setContent(updateModel.getContent());

        if (newImage != null && !newImage.isEmpty()) {
            if (existing.getLessonImageUrl() != null && !existing.getLessonImageUrl().isEmpty()) {
                fileStorageRepository.deleteFile(existing.getLessonImageUrl());
            }

            String imageUrl = fileStorageRepository.uploadFile(
                    newImage,
                    lessonPath + lessonId + "/avatar",
                    maxFileSize,
                    allowTypesImage
            );
            existing.setLessonImageUrl(imageUrl);
        }

        lessonRepository.save(existing);
        lessonRepository.flush();

        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            deleteFileIds.forEach(lessonFileService::deleteFile);
        }

        if (newLessonFiles != null && !newLessonFiles.isEmpty()) {
            lessonFileService.uploadFiles(lessonId, newLessonFiles, LessonFileType.MATERIAL);
        }

        if (newAssignmentFiles != null && !newAssignmentFiles.isEmpty()) {
            lessonFileService.uploadFiles(lessonId, newAssignmentFiles, LessonFileType.ASSIGNMENT);
        }
    }

    @Transactional
    public void deleteLesson(Long lessonId) {

        LessonModel lessonModel = lessonRepository
                .findById(lessonId)
                .orElseThrow(() -> new NotFoundException("lesson.not.foud", "Không tìm thấy bài học id: " + lessonId));

        List<LessonFileModel> lessonFileModels = lessonFileRepository.findAllByLessonId(lessonId);

        lessonFileModels.forEach(
                lessonFileModel -> lessonFileService.deleteFile(lessonFileModel.getLessonFileId()));

        if (lessonModel.getLessonImageUrl() != null && !lessonModel.getLessonImageUrl().isEmpty()) {
            fileStorageRepository.deleteFile(lessonModel.getLessonImageUrl());
        }

        lessonRepository.deleteById(lessonId);
    }


}
