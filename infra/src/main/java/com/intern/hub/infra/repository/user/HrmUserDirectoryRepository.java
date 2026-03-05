package com.intern.hub.infra.repository.user;

import com.intern.hub.core.domain.model.user.UserDirectoryModel;
import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.infra.feign.HrmInternalFeignClient;
import com.intern.hub.library.common.exception.InternalErrorException;
import feign.FeignException;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HrmUserDirectoryRepository implements UserDirectoryRepository {

    HrmInternalFeignClient hrmInternalFeignClient;

    @Override
    public boolean existsByUserId(Long userId) {
        try {
            var response = hrmInternalFeignClient.getUserByIdInternal(userId);
            return response != null && response.data() != null;
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            log.error("Failed to fetch HRM internal user by id {}", userId, ex);
            throw new InternalErrorException(
                    "hrm.user.fetch.error", "Không thể lấy thông tin user từ HRM");
        }
    }

    @Override
    public Optional<UserDirectoryModel> findByEmail(String email) {
        String normalizedEmail = email == null ? null : email.strip().toLowerCase();
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            return Optional.empty();
        }
        try {
            var response = hrmInternalFeignClient.getUserByEmailInternal(normalizedEmail);
            if (response == null || response.data() == null) {
                return Optional.empty();
            }
            var user = response.data();
            return Optional.of(
                    UserDirectoryModel.builder()
                            .userId(user.userId())
                            .email(user.email())
                            .fullName(user.fullName())
                            .role(user.role())
                            .avatarUrl(user.avatarUrl())
                            .build());
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to fetch HRM internal user by email {}", normalizedEmail, ex);
            throw new InternalErrorException(
                    "hrm.user.fetch.error", "Không thể lấy thông tin user từ HRM");
        }
    }
}
