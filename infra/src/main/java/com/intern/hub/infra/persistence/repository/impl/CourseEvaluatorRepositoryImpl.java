package com.intern.hub.infra.persistence.repository.impl;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import com.intern.hub.core.repository.course.CourseEvaluatorRepository;
import com.intern.hub.infra.persistence.repository.jpa.CourseEvaluatorEntityRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEvaluatorRepositoryImpl implements CourseEvaluatorRepository {

    CourseEvaluatorEntityRepository courseEvaluatorEntityRepository;

    @Override
    public List<EvaluatorCourseOverviewModel> findAllCourseOverviews() {
        return toOverviewModels(courseEvaluatorEntityRepository.findAllCourseOverviews());
    }

    @Override
    public List<EvaluatorCourseOverviewModel> findCourseOverviewsByEvaluatorUserId(Long evaluatorUserId) {
        return toOverviewModels(courseEvaluatorEntityRepository.findCourseOverviewsByEvaluatorUserId(evaluatorUserId));
    }

    private List<EvaluatorCourseOverviewModel> toOverviewModels(
            List<CourseEvaluatorEntityRepository.CourseOverviewProjection> rows) {
        return rows.stream()
                .map(
                        row -> {
                            long total = row.getTotalEnrollmentCount() == null ? 0L : row.getTotalEnrollmentCount();
                            long completed = row.getCompletedEnrollmentCount() == null ? 0L : row.getCompletedEnrollmentCount();
                            long notCompleted = Math.max(total - completed, 0L);
                            return EvaluatorCourseOverviewModel.builder()
                                    .courseId(row.getCourseId())
                                    .name(row.getName())
                                    .courseImageUrl(row.getCourseImageUrl())
                                    .totalEnrollmentCount(total)
                                    .completedEnrollmentCount(completed)
                                    .notCompletedEnrollmentCount(notCompleted)
                                    .build();
                        })
                .toList();
    }
}
