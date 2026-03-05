package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.reference.AdminPositionReferenceResponse;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
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
@RequestMapping("/lms/admin/references")
@Tag(name = "Admin Reference", description = "API tham chiếu cho màn admin.")
public class AdminReferenceController {

    UserDirectoryRepository userDirectoryRepository;

    @GetMapping("/positions")
    @Authenticated
    @Operation(
            summary = "Danh sách position",
            description = "Lấy danh sách position từ HRM internal qua LMS để FE dùng làm reference.")
    public ResponseApi<List<AdminPositionReferenceResponse>> getPositions() {
        var result = userDirectoryRepository.findAllPositions().stream()
                .map(position ->
                        new AdminPositionReferenceResponse(
                                position.getPositionId() == null ? null : position.getPositionId().toString(),
                                position.getName()))
                .toList();
        return ResponseApi.ok(result);
    }
}

