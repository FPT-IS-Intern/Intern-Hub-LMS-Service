package com.fis.lms_service.infra.persistence.repository.impl;

import com.fis.lms_service.core.domain.model.lesson.LessonFileModel;
import com.fis.lms_service.core.domain.model.lesson.constant.LessonFileType;
import com.fis.lms_service.core.repository.lesson.LessonFileRepository;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonEntity;
import com.fis.lms_service.infra.persistence.entity.lesson.LessonFileEntity;
import com.fis.lms_service.infra.persistence.mapper.LessonFileMapper;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonEntityRepository;
import com.fis.lms_service.infra.persistence.repository.jpa.LessonFileEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

/** Admin 1/29/2026 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonFileRepositoryImpl implements LessonFileRepository {

  LessonFileEntityRepository lessonFileEntityRepository;
  LessonEntityRepository lessonEntityRepository;

  LessonFileMapper lessonFileMapper;

  @Override
  public void save(LessonFileModel lessonFileModel) {
    LessonFileEntity lessonFileEntity = lessonFileMapper.toEntity(lessonFileModel);

    LessonEntity lessonEntity =
        lessonEntityRepository
            .findById(lessonFileModel.getLessonId())
            .orElseThrow(EntityNotFoundException::new);

    lessonFileEntity.setLessonEntity(lessonEntity);
    lessonFileEntityRepository.save(lessonFileEntity);
  }

  @Override
  public Long getTotalSizeByLessonId(Long lessonId, LessonFileType lessonFileType) {
    Long total = lessonFileEntityRepository.sumFileSizeByLessonId(lessonId, lessonFileType);
    return total != null ? total : 0L;
  }

  @Override
  public List<LessonFileModel> findAllByLessonId(Long lessonId) {
    List<LessonFileEntity> entities = lessonFileEntityRepository.findAllByLessonId(lessonId);

    return entities.stream().map(lessonFileMapper::toModel).toList();
  }

  @Override
  public LessonFileModel findById(Long lessonFileId) {
    LessonFileEntity lessonFileEntity =
        lessonFileEntityRepository.findById(lessonFileId).orElseThrow(EntityNotFoundException::new);

    return lessonFileMapper.toModel(lessonFileEntity);
  }

  @Override
  public void deleteById(Long lessonFileId) {
    lessonFileEntityRepository.deleteById(lessonFileId);
  }
}
