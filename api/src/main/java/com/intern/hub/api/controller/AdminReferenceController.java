package com.intern.hub.api.controller;

import com.intern.hub.api.dto.response.reference.AdminPositionReferenceResponse;
import com.intern.hub.api.dto.response.reference.AdminUserReferenceResponse;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.starter.security.annotation.Authenticated;
import com.intern.hub.starter.security.annotation.HasPermission;
import com.intern.hub.starter.security.entity.Action;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  @HasPermission(resource = "quan-ly-khoa-hoc", action = Action.READ)
  @Operation(
      summary = "Danh sách position",
      description = "Lấy danh sách position từ HRM internal qua LMS để FE dùng làm reference.")
  public ResponseApi<List<AdminPositionReferenceResponse>> getPositions() {
    var result =
        userDirectoryRepository.findAllPositions().stream()
            .map(
                position ->
                    new AdminPositionReferenceResponse(
                        position.getPositionId() == null
                            ? null
                            : position.getPositionId().toString(),
                        position.getName()))
            .toList();
    return ResponseApi.ok(result);
  }

  @GetMapping("/users/by-email")
  @Authenticated
  @HasPermission(resource = "quan-ly-khoa-hoc", action = Action.READ)
  @Operation(
      summary = "Tra cứu user theo email",
      description = "Lấy thông tin user reference theo email để FE hiển thị candidate.")
  public ResponseApi<AdminUserReferenceResponse> getUserByEmail(
      @RequestParam("email") String email) {
    if (email == null || email.isBlank()) {
      throw new BadRequestException("email.invalid", "email không hợp lệ");
    }

    var user =
        userDirectoryRepository
            .findByEmail(email)
            .map(
                item ->
                    new AdminUserReferenceResponse(
                        item.getUserId() == null ? null : item.getUserId().toString(),
                        item.getEmail(),
                        item.getFullName(),
                        item.getRole(),
                        item.getAvatarUrl()))
            .orElse(null);
    return ResponseApi.ok(user);
  }
}
