package com.intern.hub.core.service.evaluator;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import com.intern.hub.core.repository.course.CourseEvaluatorRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluatorService {

    CourseEvaluatorRepository courseEvaluatorRepository;

    @Transactional(readOnly = true)
    public List<EvaluatorCourseOverviewModel> getEvaluatorCourseOverviews(Long evaluatorUserId) {
        return courseEvaluatorRepository.findCourseOverviewsByEvaluatorUserId(evaluatorUserId);
    }
}

