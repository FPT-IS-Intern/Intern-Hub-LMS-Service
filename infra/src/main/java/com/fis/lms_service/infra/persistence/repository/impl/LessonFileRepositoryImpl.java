package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;
import com.fis.lms_service.infra.persistence.mapper.LessonFileMapper;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonFileJpaRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonJpaRepository;
import com.fis.lms_service.infra.storage.S3StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 1/29/2026
 *
 **/
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonFileRepositoryImpl implements LessonFileRepository {

    LessonFileJpaRepository lessonFileJpaRepository;
    LessonJpaRepository lessonJpaRepository;

    S3StorageService s3StorageService;
    LessonFileMapper lessonFileMapper;

    @Override
    public void save(LessonFileModel model) {
        LessonFileEntity lessonFileEntity = lessonFileMapper.toEntity(model);

        LessonEntity lessonEntity = lessonJpaRepository
                .findById(model.getLessonId())
                .orElseThrow(EntityNotFoundException::new);

        lessonFileEntity.setLessonEntity(lessonEntity);
        lessonFileJpaRepository.save(lessonFileEntity);
    }

    @Override
    public Long getTotalSizeByLessonId(Long lessonId) {
        Long total = lessonFileJpaRepository.sumFileSizeByLessonId(lessonId);
        return total != null ? total : 0L;
    }

    @Override
    public List<LessonFileModel> findAllByLessonId(Long lessonId) {
        List<LessonFileEntity> entities = lessonFileJpaRepository
                .findAllByLessonEntity_LessonId(lessonId);

        return entities
                .stream()
                .map(lessonFileMapper::toModel)
                .toList();
    }

    @Override
    public void deleteById(Long lessonFileId) {
        LessonFileEntity entity = lessonFileJpaRepository
                .findById(lessonFileId)
                .orElseThrow(EntityNotFoundException::new);

        s3StorageService.deleteFile(entity.getFileUrl());
        lessonFileJpaRepository.delete(entity);
    }
}
