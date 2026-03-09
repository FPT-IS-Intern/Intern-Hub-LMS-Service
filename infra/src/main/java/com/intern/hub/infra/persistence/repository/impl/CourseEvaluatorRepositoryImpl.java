package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import com.intern.hub.core.repository.course.CourseEvaluatorRepository;
import com.intern.hub.infra.persistence.repository.jpa.CourseEvaluatorEntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEvaluatorRepositoryImpl implements CourseEvaluatorRepository {

    CourseEvaluatorEntityRepository courseEvaluatorEntityRepository;

    @Override
    public Page<EvaluatorCourseOverviewModel> findAllCourseOverviews(Long evaluatorUserId, Pageable pageable) {
        return toOverviewModels(courseEvaluatorEntityRepository.findAllCourseOverviews(evaluatorUserId, pageable));
    }

    @Override
    public Page<EvaluatorCourseOverviewModel> findCourseOverviewsByEvaluatorUserId(
            Long evaluatorUserId, Pageable pageable) {
        return toOverviewModels(
                courseEvaluatorEntityRepository.findCourseOverviewsByEvaluatorUserId(evaluatorUserId, pageable));
    }

    private Page<EvaluatorCourseOverviewModel> toOverviewModels(
            Page<CourseEvaluatorEntityRepository.CourseOverviewProjection> rows) {
        return rows.map(
                row -> {
                    long total = row.getTotalEnrollmentCount() == null ? 0L : row.getTotalEnrollmentCount();
                    long completed =
                            row.getCompletedEnrollmentCount() == null ? 0L : row.getCompletedEnrollmentCount();
                    long notCompleted = Math.max(total - completed, 0L);
                    return EvaluatorCourseOverviewModel.builder()
                            .courseId(row.getCourseId())
                            .name(row.getName())
                            .courseImageUrl(row.getCourseImageUrl())
                            .totalEnrollmentCount(total)
                            .completedEnrollmentCount(completed)
                            .notCompletedEnrollmentCount(notCompleted)
                            .canEvaluate(row.getCanEvaluate())
                            .build();
                });
    }
}
