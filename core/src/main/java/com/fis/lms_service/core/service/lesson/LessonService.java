package com.fis.lms_service.core.service.lesson;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.LessonModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import com.fis.lms_service.core.repository.FileStorageRepository;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.core.repository.lesson.LessonRepository;
import com.fis.lms_service.core.service.storage.StorageObjectLifecycleManager;
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

import java.util.List;

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

    @Transactional
    /**
     * Tạo bài học mới, upload ảnh đại diện và các file liên quan (tài liệu/bài tập) nếu có.
     */
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

    @Transactional(readOnly = true)
    /**
     * Lấy danh sách bài học theo phân trang và gắn URL đầy đủ cho ảnh bài học nếu có.
     */
    public Page<@NonNull LessonModel> getLessons(Pageable pageable) {
        var res = lessonRepository.findAll(pageable);

        res.getContent().forEach(this::applyBucketUrl);
        return res;
    }

    @Transactional(readOnly = true)
    /**
     * Lấy chi tiết một bài học theo id và gắn URL đầy đủ cho ảnh nếu có.
     */
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

    @Transactional
    /**
     * Cập nhật nội dung bài học, thay ảnh đại diện và thêm/xóa file tài liệu/bài tập nếu có.
     */
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

    @Transactional
    /**
     * Xóa bài học và toàn bộ file liên quan (ảnh đại diện, tài liệu/bài tập) nếu có.
     */
    public void deleteLesson(Long lessonId) {

        LessonModel lessonModel =
                lessonRepository
                        .findById(lessonId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "lesson.not.found", "Không tìm thấy bài học id: " + lessonId));

        List<LessonFileModel> lessonFileModels = lessonFileRepository.findAllByLessonId(lessonId);

        lessonFileModels.forEach(
                lessonFileModel -> lessonFileService.deleteFile(lessonFileModel.getLessonFileId()));

        if (hasText(lessonModel.getLessonImageUrl())) {
            storageObjectLifecycleManager.deleteAfterCommit(lessonModel.getLessonImageUrl());
        }

        lessonRepository.deleteById(lessonId);
    }
}
