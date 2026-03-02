package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.lesson.LessonModel;
import com.intern.hub.core.repository.lesson.LessonRepository;
import com.intern.hub.infra.persistence.entity.lesson.LessonEntity;
import com.intern.hub.infra.persistence.mapper.LessonEntityMapper;
import com.intern.hub.infra.persistence.repository.jpa.LessonEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Admin 1/29/2026
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonRepositoryImpl implements LessonRepository {

    LessonEntityRepository lessonEntityRepository;

    LessonEntityMapper lessonMapper;

    @Override
    /** Lưu hoặc cập nhật bài học: map model -> entity, sau đó persist và map ngược lại. */
    public LessonModel save(LessonModel lessonModel) {
        LessonEntity lessonEntity;
        if (lessonModel.getLessonId() == null) {
            lessonEntity = new LessonEntity();
        } else {
            lessonEntity =
                    lessonEntityRepository.findById(lessonModel.getLessonId()).orElse(new LessonEntity());
        }

        lessonMapper.updateEntityFromModel(lessonModel, lessonEntity);

        LessonEntity savedLessonEntity = lessonEntityRepository.save(lessonEntity);

        return lessonMapper.toModel(savedLessonEntity);
    }

    @Override
    /** Tìm bài học theo id (trả về Optional). */
    public Optional<LessonModel> findById(Long lessonId) {
        return lessonEntityRepository.findById(lessonId).map(lessonMapper::toModel);
    }

    @Override
    /** Xóa bài học theo id. */
    public void deleteById(Long id) {
        lessonEntityRepository.deleteById(id);
    }

    @Override
    /** Lấy danh sách bài học theo phân trang. */
    public Page<@NonNull LessonModel> findAll(Pageable pageable) {
        return lessonEntityRepository.findAll(pageable).map(lessonMapper::toModel);
    }

    @Override
    public List<LessonModel> findAllByIds(List<Long> lessonIds) {
        if (lessonIds == null || lessonIds.isEmpty()) {
            return List.of();
        }

        var entities = lessonEntityRepository.findAllById(lessonIds);
        var byId = new HashMap<Long, LessonEntity>(entities.size());
        for (LessonEntity entity : entities) {
            byId.put(entity.getLessonId(), entity);
        }

        return lessonIds.stream()
                .map(byId::get)
                .filter(entity -> entity != null)
                .map(lessonMapper::toModel)
                .toList();
    }

    @Override
    /** Flush để đảm bảo dữ liệu đã được ghi xuống DB ngay. */
    public void flush() {
        lessonEntityRepository.flush();
    }
}
