package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.evaluator.EvaluatorCourseOverviewResponse;
import com.intern.hub.api.util.PaginationUtils;
import com.intern.hub.api.util.UserContext;
import com.intern.hub.core.service.evaluator.EvaluatorService;
import com.intern.hub.library.common.dto.PaginatedData;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.starter.security.annotation.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lms/evaluator")
@Tag(name = "Evaluator", description = "API cho evaluator xem thong ke enrollment theo khoa hoc.")
public class EvaluatorController {

    EvaluatorService evaluatorService;

    @GetMapping("/courses")
    @Authenticated
    @Operation(
            summary = "Danh sach khoa hoc evaluator",
            description = "Lay danh sach khoa hoc kem so luong enrollment, hoan thanh, chua hoan thanh "
                    + "va co the danh gia hay khong.")
    public ResponseApi<PaginatedData<EvaluatorCourseOverviewResponse>> getEvaluatorCourses(
            @RequestParam(value = "onlyEvaluable", required = false, defaultValue = "false")
            boolean onlyEvaluable,
            @PageableDefault(size = 10) Pageable pageable) {
        Long evaluatorUserId = UserContext.requiredUserId();
        var page = evaluatorService.getCourseOverviews(evaluatorUserId, onlyEvaluable, pageable);
        var result = PaginationUtils.toPaginatedData(
                page,
                item ->
                        new EvaluatorCourseOverviewResponse(
                                item.getCourseId() == null ? null : item.getCourseId().toString(),
                                item.getName(),
                                item.getCourseImageUrl(),
                                item.getTotalEnrollmentCount(),
                                item.getCompletedEnrollmentCount(),
                                item.getNotCompletedEnrollmentCount(),
                                item.isCanEvaluate()));
        return ResponseApi.ok(result);
    }
}
