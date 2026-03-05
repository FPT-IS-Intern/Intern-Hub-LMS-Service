package com.intern.hub.core.repository.course;

import java.util.List;

public interface CoursePositionAssignmentRepository {

    void saveCoursePositions(Long courseId, List<Long> positionIds);

    void replaceCoursePositions(Long courseId, List<Long> positionIds);

    List<Long> findPositionIdsByCourseId(Long courseId);
}
