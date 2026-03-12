# Intern Hub LMS Service

Service LMS trong hệ sinh thái Intern Hub, quản lý khóa học, bài học, ghi danh và nộp bài.

**Phạm vi nghiệp vụ**
- Thuộc service `intern-hub-lms-service`, chỉ xử lý trong các module `api`, `core`, `infra`.
- Không chứa logic liên service; dùng `Intern-Hub-Common-Library` qua JitPack.

**Nghiệp vụ chính**
1. Quản trị khóa học (admin).
Chi tiết: tạo/cập nhật/xóa khóa học; ảnh đại diện bắt buộc khi tạo; có thể gắn danh sách bài học theo thứ tự truyền vào.
2. Quản trị bài học (admin).
Chi tiết: tạo/cập nhật/xóa bài học; ảnh đại diện tùy chọn; quản lý file tài liệu (MATERIAL) và file bài tập (ASSIGNMENT).
3. Ghi danh khóa học (user).
Chi tiết: tạo hoặc cập nhật `CourseEnrollment` sang `IN_PROGRESS`, tự động tạo `LessonEnrollment` cho toàn bộ bài học của khóa học nếu chưa có.
Chi tiết thêm: `LessonEnrollment` được hiểu theo ngữ cảnh course, tức là khóa duy nhất là `(course_enrollment_id, lesson_id)`, không phải tiến độ global theo `user_id + lesson_id`.
4. Tra cứu bài học (user).
Chi tiết: lấy danh sách bài học toàn hệ hoặc theo khóa; với API theo course, `lessonEnrollmentId` luôn được tra cứu theo đúng `courseEnrollment` của user. Với API lesson toàn hệ, chỉ trả `lessonEnrollmentId` khi user chỉ có đúng 1 ngữ cảnh course cho lesson đó; nếu lesson xuất hiện ở nhiều course của cùng user thì trường enrollment sẽ để `null` để tránh mơ hồ.
5. Nộp bài (user).
Chi tiết: yêu cầu ít nhất 1 file; comment tùy chọn; thay thế toàn bộ attachments cũ nếu nộp lại; cập nhật `LessonProgress` sang `COMPLETED` và cập nhật `CourseProgress` nếu hoàn tất tất cả bài học.

**Quy tắc nghiệp vụ & ràng buộc**
- Ảnh khóa học bắt buộc khi tạo; ảnh bài học tùy chọn.
- Tài liệu bài học có giới hạn tổng dung lượng (theo `aws.s3.max-total-size`).
- Kiểu file cho ảnh: `image/(png|jpeg|jpg)`.
- Kiểu file tài liệu: PDF/DOC/DOCX/XLS/XLSX và ảnh.
- Ảnh và file được lưu S3; URL ảnh được ghép thêm `bucket-url`, file bài học dùng URL private.
- Khóa duy nhất:
  - `course_enrollments`: (course_id, user_id)
  - `lesson_enrollments`: (course_enrollment_id, lesson_id)
  - `lesson_submissions`: (lesson_enrollment_id)
- Hướng thiết kế hiện tại:
  - Tiến độ/nộp bài của lesson là theo từng course enrollment.
  - Cùng một `lesson_id` có thể xuất hiện ở nhiều course; khi đó user có thể có nhiều `lesson_enrollment` khác nhau cho lesson đó, tương ứng từng course.
- Quy tắc cập nhật course:
  - Khi thêm lesson mới vào course, hệ thống tự sync `lesson_enrollment` còn thiếu cho toàn bộ user đã enroll course đó.
  - Khi bỏ lesson khỏi course, hệ thống không xóa lịch sử `lesson_enrollment`/`submission` cũ để tránh mất dữ liệu và tránh ảnh hưởng course khác cũng dùng cùng lesson.
  - Các API đọc theo `courseEnrollment` chỉ lấy submission/progress của những lesson hiện còn thuộc course.

**Dữ liệu chính**
- Course: thông tin khóa học và ảnh đại diện.
- Lesson: nội dung bài học, giới thiệu, yêu cầu hoàn thành, ảnh.
- CourseLesson: liên kết khóa học - bài học, có `orderIndex`.
- CourseEnrollment: tiến độ khóa học (`IN_PROGRESS`, `COMPLETED`, `CANCELED`).
- LessonEnrollment: tiến độ bài học (`IN_PROGRESS`, `COMPLETED`, `CANCELED`).
- LessonSubmission: trạng thái nộp (`SUBMITTED`, `NOT_SUBMITTED`) và lần nộp gần nhất.
- SubmissionAttachment/SubmissionComment: file và ghi chú nộp bài.
- LessonFile: file tài liệu/assignment gắn với bài học.
- CourseEvaluator/CoursePosition: quan hệ đánh giá và vị trí cho khóa học.

**API nghiệp vụ (tóm tắt)**
- `POST /admin/courses` (multipart): tạo khóa học + ảnh + `lessonIds`.
- `GET /admin/courses`: danh sách khóa học (phân trang).
- `GET /admin/courses/{courseId}`: chi tiết khóa học.
- `PUT /admin/courses/{courseId}` (multipart): cập nhật khóa học.
- `DELETE /admin/courses/{courseId}`: xóa khóa học.
- `POST /admin/lessons` (multipart): tạo bài học + ảnh + file.
- `GET /admin/lessons`: danh sách bài học (phân trang).
- `GET /admin/lessons/{lessonId}`: chi tiết bài học.
- `PUT /admin/lessons/{lessonId}` (multipart): cập nhật bài học + xóa file theo `deleteFileIds`.
- `DELETE /admin/lessons/{lessonId}`: xóa bài học.
- `POST /courses/{courseId}/enroll`: ghi danh khóa học.
- `GET /courses/{courseId}/lessons`: danh sách bài học theo khóa, `userId` tùy chọn.
- `GET /lessons`: danh sách bài học toàn hệ, `userId` tùy chọn.
- `GET /lessons/{lessonId}`: chi tiết bài học, `userId` tùy chọn.
- `POST /lesson-enrollments/{lessonEnrollmentId}/submit` (multipart): nộp bài.

**Cấu hình liên quan nghiệp vụ**
- DB schema: `schema_lms` (Liquibase tạo bảng và ràng buộc).
- S3 paths: `lessons/`, `courses/`, `submissions/`.
- Giới hạn dung lượng: `aws.s3.max-total-size` (mặc định 20MB).
