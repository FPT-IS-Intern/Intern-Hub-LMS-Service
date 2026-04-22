package com.intern.hub.infra.persistence.repository.jpa;

import com.intern.hub.infra.persistence.entity.submission.SubmissionAttachmentEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 1/26/2026
 */
@Repository
public interface SubmissionAttachmentEntityRepository
        extends JpaRepository<@NonNull SubmissionAttachmentEntity, @NonNull Long> {

    @Modifying
    @Query("DELETE FROM SubmissionAttachmentEntity sa WHERE sa.lessonSubmissionEntity.lessonEnrollmentEntity.courseEnrollmentEntity.courseEntity.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);

    List<SubmissionAttachmentEntity> findByLessonSubmissionEntity_LessonSubmissionId(
            Long lessonSubmissionId);

    void deleteByLessonSubmissionEntity_LessonSubmissionId(Long lessonSubmissionId);

    void deleteBySubmissionAttachmentIdIn(List<Long> submissionAttachmentIds);
}
