package com.intern.hub.core.service.evaluator;

import com.intern.hub.core.domain.model.course.EvaluatorCourseOverviewModel;
import com.intern.hub.core.repository.course.CourseEvaluatorRepository;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluatorService {

    CourseEvaluatorRepository courseEvaluatorRepository;
    UserDirectoryRepository userDirectoryRepository;

    @Transactional(readOnly = true)
    public Page<EvaluatorCourseOverviewModel> getCourseOverviews(
            Long evaluatorUserId, boolean onlyEvaluable, Pageable pageable) {
        if (onlyEvaluable) {
            if (!userDirectoryRepository.existsByUserId(evaluatorUserId)) {
                throw new NotFoundException("hrm.user.not.found", "Khong tim thay user trong HRM");
            }
            return courseEvaluatorRepository.findCourseOverviewsByEvaluatorUserId(evaluatorUserId, pageable);
        }
        return courseEvaluatorRepository.findAllCourseOverviews(evaluatorUserId, pageable);
    }
}
