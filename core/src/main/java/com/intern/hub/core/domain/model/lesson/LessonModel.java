package com.intern.hub.core.domain.model.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Admin 1/27/2026
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonModel {

    Long lessonId;
    String name;
    String introduction;
    String content;
    String requirements;
    String lessonImageUrl;
    Long createdAt;
    Long updatedAt;
}
