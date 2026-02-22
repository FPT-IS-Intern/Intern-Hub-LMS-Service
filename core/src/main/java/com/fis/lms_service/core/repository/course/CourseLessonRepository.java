package com.fis.lms_service.core.repository.course;

import java.util.List;

public interface CourseLessonRepository {

  List<Long> findLessonIdsByCourseId(Long courseId);

  void saveCourseLessons(Long courseId, List<Long> lessonIds);
}
