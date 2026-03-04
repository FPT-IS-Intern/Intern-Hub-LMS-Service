package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.submission.SubmissionCommentModel;
import com.intern.hub.core.repository.submission.SubmissionCommentRepository;
import com.intern.hub.infra.persistence.entity.submission.SubmissionCommentEntity;
import com.intern.hub.infra.persistence.repository.jpa.LessonSubmissionEntityRepository;
import com.intern.hub.infra.persistence.repository.jpa.SubmissionCommentEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionCommentRepositoryImpl implements SubmissionCommentRepository {

    SubmissionCommentEntityRepository submissionCommentEntityRepository;
    LessonSubmissionEntityRepository lessonSubmissionEntityRepository;

    @Override
    public SubmissionCommentModel save(SubmissionCommentModel model) {
        SubmissionCommentEntity entity = new SubmissionCommentEntity();
        entity.setLessonSubmissionEntity(
                lessonSubmissionEntityRepository.getReferenceById(model.getLessonSubmissionId()));
        entity.setUserId(model.getUserId());
        entity.setContent(model.getContent());
        entity.setCommentAt(model.getCommentAt());

        SubmissionCommentEntity saved = submissionCommentEntityRepository.save(entity);

        return SubmissionCommentModel.builder()
                .submissionCommentId(saved.getSubmissionCommentId())
                .lessonSubmissionId(saved.getLessonSubmissionEntity().getLessonSubmissionId())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .commentAt(saved.getCommentAt())
                .build();
    }

    @Override
    public Optional<SubmissionCommentModel> findLatestByLessonSubmissionId(Long lessonSubmissionId) {
        return submissionCommentEntityRepository
                .findFirstByLessonSubmissionEntity_LessonSubmissionIdOrderByCommentAtDesc(lessonSubmissionId)
                .map(this::toModel);
    }

    private SubmissionCommentModel toModel(SubmissionCommentEntity entity) {
        return SubmissionCommentModel.builder()
                .submissionCommentId(entity.getSubmissionCommentId())
                .lessonSubmissionId(entity.getLessonSubmissionEntity().getLessonSubmissionId())
                .userId(entity.getUserId())
                .content(entity.getContent())
                .commentAt(entity.getCommentAt())
                .build();
    }
}
