package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.submission.SubmissionCommentEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Admin 1/26/2026
 */
@Repository
public interface SubmissionCommentEntityRepository
        extends JpaRepository<@NonNull SubmissionCommentEntity, @NonNull Long> {

    java.util.Optional<SubmissionCommentEntity> findFirstByLessonSubmissionEntity_LessonSubmissionIdOrderByCommentAtDesc(
            Long lessonSubmissionId);

    java.util.List<SubmissionCommentEntity> findByLessonSubmissionEntity_LessonSubmissionIdOrderByCommentAtDesc(
            Long lessonSubmissionId);
}
