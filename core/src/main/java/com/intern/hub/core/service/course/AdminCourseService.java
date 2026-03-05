package com.intern.hub.core.service.course;

import com.intern.hub.core.domain.model.course.CourseModel;
import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.core.repository.course.CourseEvaluatorAssignmentRepository;
import com.intern.hub.core.repository.course.CourseLessonRepository;
import com.intern.hub.core.repository.course.CoursePositionAssignmentRepository;
import com.intern.hub.core.repository.course.CourseRepository;
import com.intern.hub.core.service.storage.StorageObjectLifecycleManager;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Nhóm nghiệp vụ quản trị khóa học (admin): tạo/cập nhật/xóa khóa học, lấy danh sách/chi tiết và
 * quản lý ảnh đại diện khóa học.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCourseService {

    CourseRepository courseRepository;
    CourseLessonRepository courseLessonRepository;
    CourseEvaluatorAssignmentRepository courseEvaluatorAssignmentRepository;
    CoursePositionAssignmentRepository coursePositionAssignmentRepository;
    FileStorageRepository fileStorageRepository;
    StorageObjectLifecycleManager storageObjectLifecycleManager;

    @NonFinal
    @Value("${aws.s3.paths.course}")
    String coursePath;

    @NonFinal
    @Value("${aws.s3.max-total-size}")
    Long maxTotalSize;

    @NonFinal
    @Value("${aws.s3.allow-types.image}")
    String allowTypesImage;

    /**
     * Tạo mới khóa học. Ảnh đại diện là bắt buộc; danh sách bài học (nếu có) sẽ được liên kết theo
     * thứ tự đã truyền.
     */
    @Transactional
    public void createCourse(
            CourseModel model,
            MultipartFile image,
            List<Long> lessonIds,
            List<Long> evaluatorUserIds,
            List<Long> positionIds,
            Long actorId) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("course.image.required", "Ảnh khóa học là bắt buộc");
        }

        CourseModel saved = courseRepository.save(model);
        Long courseId = saved.getCourseId();

        if (hasItems(lessonIds)) {
            courseLessonRepository.saveCourseLessons(courseId, distinctOrdered(lessonIds));
        }
        if (hasItems(evaluatorUserIds)) {
            courseEvaluatorAssignmentRepository.saveCourseEvaluators(
                    courseId, distinctOrdered(evaluatorUserIds));
        }
        if (hasItems(positionIds)) {
            coursePositionAssignmentRepository.saveCoursePositions(
                    courseId, distinctOrdered(positionIds));
        }

        String imageUrl =
                fileStorageRepository.uploadFile(
                        image, buildCourseImagePath(courseId), actorId, maxTotalSize, allowTypesImage);
        storageObjectLifecycleManager.cleanupOnRollback(imageUrl, actorId);

        saved.setCourseImageUrl(imageUrl);
        courseRepository.save(saved);
    }

    /**
     * Lấy danh sách khóa học có phân trang cho màn admin.
     */
    @Transactional(readOnly = true)
    public Page<@NonNull CourseModel> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    /**
     * Lấy chi tiết một khóa học theo id.
     */
    @Transactional(readOnly = true)
    public CourseModel getCourse(Long courseId) {
        return courseRepository
                .findById(courseId)
                .orElseThrow(
                        () ->
                                new NotFoundException("course.not.found", "Không tìm thấy khóa học id: " + courseId));
    }

    /**
     * Lấy danh sách lesson id đang gắn với khóa học.
     */
    @Transactional(readOnly = true)
    public List<Long> getCourseLessonIds(Long courseId) {
        return courseLessonRepository.findLessonIdsByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<Long> getCourseEvaluatorUserIds(Long courseId) {
        return courseEvaluatorAssignmentRepository.findEvaluatorUserIdsByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<Long> getCoursePositionIds(Long courseId) {
        return coursePositionAssignmentRepository.findPositionIdsByCourseId(courseId);
    }

    /**
     * Cập nhật thông tin khóa học và thay ảnh đại diện nếu có ảnh mới.
     */
    @Transactional
    public void updateCourse(
            Long courseId,
            CourseModel updateModel,
            MultipartFile newImage,
            List<Long> lessonIds,
            List<Long> evaluatorUserIds,
            List<Long> positionIds,
            Long actorId) {
        CourseModel existing = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new NotFoundException(
                        "course.not.found", "Không tìm thấy khóa học id: " + courseId
                ));

        existing.setName(updateModel.getName());
        existing.setDescription(updateModel.getDescription());

        if (lessonIds != null) {
            courseLessonRepository.replaceCourseLessons(courseId, distinctOrdered(lessonIds));
        }
        if (evaluatorUserIds != null) {
            courseEvaluatorAssignmentRepository.replaceCourseEvaluators(
                    courseId, distinctOrdered(evaluatorUserIds));
        }
        if (positionIds != null) {
            coursePositionAssignmentRepository.replaceCoursePositions(
                    courseId, distinctOrdered(positionIds));
        }

        if (newImage != null && !newImage.isEmpty()) {
            String oldImageUrl = existing.getCourseImageUrl();
            String newImageUrl =
                    fileStorageRepository.uploadFile(
                            newImage, buildCourseImagePath(courseId), actorId, maxTotalSize, allowTypesImage);
            storageObjectLifecycleManager.cleanupOnRollback(newImageUrl, actorId);

            existing.setCourseImageUrl(newImageUrl);
            if (hasText(oldImageUrl)) {
                storageObjectLifecycleManager.deleteAfterCommit(oldImageUrl, actorId);
            }
        }

        courseRepository.save(existing);
    }

    /**
     * Xóa khóa học và các quan hệ liên quan. Ảnh đại diện cũ sẽ được xóa khỏi storage sau khi
     * transaction commit thành công.
     */
    @Transactional
    public void deleteCourse(Long courseId, Long actorId) {
        CourseModel courseModel =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "course.not.found", "Không tìm thấy khóa học id: " + courseId));

        courseRepository.deleteWithRelationsById(courseId);
        if (hasText(courseModel.getCourseImageUrl())) {
            storageObjectLifecycleManager.deleteAfterCommit(courseModel.getCourseImageUrl(), actorId);
        }
    }

    // =========================== Utilities ===========================
    private static boolean hasItems(List<?> items) {
        return items != null && !items.isEmpty();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static List<Long> distinctOrdered(List<Long> lessonIds) {
        if (lessonIds == null || lessonIds.isEmpty()) {
            return List.of();
        }
        Set<Long> unique = new java.util.LinkedHashSet<>();
        for (Long lessonId : lessonIds) {
            if (lessonId != null) {
                unique.add(lessonId);
            }
        }
        return new ArrayList<>(unique);
    }

    private String buildCourseImagePath(Long courseId) {
        return coursePath + courseId + "/avatar";
    }
}
