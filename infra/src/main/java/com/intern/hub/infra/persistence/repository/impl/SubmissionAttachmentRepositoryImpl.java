package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.submission.SubmissionAttachmentModel;
import com.intern.hub.core.repository.submission.SubmissionAttachmentRepository;
import com.intern.hub.infra.persistence.entity.submission.SubmissionAttachmentEntity;
import com.intern.hub.infra.persistence.repository.jpa.LessonSubmissionEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.SubmissionAttachmentEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionAttachmentRepositoryImpl implements SubmissionAttachmentRepository {

    SubmissionAttachmentEntityRepository submissionAttachmentEntityRepository;
    LessonSubmissionEntityRepository lessonSubmissionEntityRepository;

    @Override
    public List<SubmissionAttachmentModel> findByLessonSubmissionId(Long lessonSubmissionId) {
        return submissionAttachmentEntityRepository
                .findByLessonSubmissionEntity_LessonSubmissionId(lessonSubmissionId)
                .stream()
                .map(
                        entity ->
                                SubmissionAttachmentModel.builder()
                                        .submissionAttachmentId(entity.getSubmissionAttachmentId())
                                        .lessonSubmissionId(entity.getLessonSubmissionEntity().getLessonSubmissionId())
                                        .fileUrl(entity.getFileUrl())
                                        .fileName(entity.getFileName())
                                        .fileSize(entity.getFileSize())
                                        .build())
                .toList();
    }

    @Override
    public void deleteByLessonSubmissionId(Long lessonSubmissionId) {
        submissionAttachmentEntityRepository.deleteByLessonSubmissionEntity_LessonSubmissionId(
                lessonSubmissionId);
    }

    @Override
    public void saveAll(List<SubmissionAttachmentModel> models) {
        if (models == null || models.isEmpty()) {
            return;
        }

        var lessonSubmission =
                lessonSubmissionEntityRepository.getReferenceById(models.get(0).getLessonSubmissionId());
        List<SubmissionAttachmentEntity> entities = new ArrayList<>(models.size());

        for (SubmissionAttachmentModel model : models) {
            SubmissionAttachmentEntity entity = new SubmissionAttachmentEntity();
            entity.setLessonSubmissionEntity(lessonSubmission);
            entity.setFileUrl(model.getFileUrl());
            entity.setFileName(model.getFileName());
            entity.setFileSize(model.getFileSize());
            entities.add(entity);
        }

        submissionAttachmentEntityRepository.saveAll(entities);
    }
}
