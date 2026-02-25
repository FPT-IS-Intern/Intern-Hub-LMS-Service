package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;
import com.fis.lms_service.infra.persistence.mapper.LessonFileEntityMapper;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonFileEntityRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Admin 1/29/2026
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonFileRepositoryImpl implements LessonFileRepository {

    LessonFileEntityRepository lessonFileEntityRepository;
    LessonEntityRepository lessonEntityRepository;

    LessonFileEntityMapper lessonFileMapper;

    @Override
    /** Lưu file bài học: map model -> entity, gán quan hệ lesson, rồi persist. */
    public void save(LessonFileModel lessonFileModel) {
        LessonFileEntity lessonFileEntity = lessonFileMapper.toEntity(lessonFileModel);

        LessonEntity lessonEntity =
                lessonEntityRepository
                        .findById(lessonFileModel.getLessonId())
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "lesson.not.found",
                                                "Không tìm thấy bài học id: " + lessonFileModel.getLessonId()));

        lessonFileEntity.setLessonEntity(lessonEntity);
        lessonFileEntityRepository.save(lessonFileEntity);
    }

    @Override
    /** Tính tổng dung lượng file theo bài học và loại file. */
    public Long getTotalSizeByLessonId(Long lessonId, LessonFileType lessonFileType) {
        Long total = lessonFileEntityRepository.sumFileSizeByLessonId(lessonId, lessonFileType);
        return total != null ? total : 0L;
    }

    @Override
    /** Lấy danh sách file theo lessonId. */
    public List<LessonFileModel> findAllByLessonId(Long lessonId) {
        List<LessonFileEntity> entities = lessonFileEntityRepository.findAllByLessonId(lessonId);

        return entities.stream().map(lessonFileMapper::toModel).toList();
    }

    @Override
    /** Tìm file theo id (trả về Optional). */
    public Optional<LessonFileModel> findById(Long lessonFileId) {
        return lessonFileEntityRepository.findById(lessonFileId).map(lessonFileMapper::toModel);
    }

    @Override
    /** Xóa file theo id. */
    public void deleteById(Long lessonFileId) {
        lessonFileEntityRepository.deleteById(lessonFileId);
    }
}
