package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.lesson.LessonFileModel;
import com.intern.hub.core.domain.model.lesson.constant.LessonFileType;
import com.intern.hub.core.repository.lesson.LessonFileRepository;
import com.intern.hub.infra.persistence.entity.lesson.LessonEntity;
import com.intern.hub.infra.persistence.entity.lesson.LessonFileEntity;
import com.intern.hub.infra.persistence.mapper.LessonFileEntityMapper;
import com.intern.hub.infra.persistence.repository.jpa.LessonEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.LessonFileEntityRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
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
