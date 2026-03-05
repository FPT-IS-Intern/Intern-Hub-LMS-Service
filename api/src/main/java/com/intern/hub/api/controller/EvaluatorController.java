package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.evaluator.EvaluatorCourseOverviewResponse;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.service.evaluator.EvaluatorService;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/evaluator")
@Tag(
        name = "Evaluator",
        description = "API cho evaluator xem thống kê enrollment theo khóa học được phân công.")
public class EvaluatorController {

    EvaluatorService evaluatorService;

    @GetMapping("/courses")
    @Authenticated
    @Operation(
            summary = "Danh sách khóa học evaluator",
            description = "Lấy danh sách khóa học của evaluator kèm số lượng enrollment, hoàn thành và chưa hoàn thành.")
    public ResponseApi<List<EvaluatorCourseOverviewResponse>> getEvaluatorCourses() {
        Long evaluatorUserId = UserContext.requiredUserId();
        var result = evaluatorService
                .getEvaluatorCourseOverviews(evaluatorUserId)
                .stream()
                .map(item ->
                        new EvaluatorCourseOverviewResponse(
                                item.getCourseId() == null ? null : item.getCourseId().toString(),
                                item.getName(),
                                item.getCourseImageUrl(),
                                item.getTotalEnrollmentCount(),
                                item.getCompletedEnrollmentCount(),
                                item.getNotCompletedEnrollmentCount()
                        ))
                .toList();
        return ResponseApi.ok(result);
    }
}

