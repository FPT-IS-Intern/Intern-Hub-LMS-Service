package com.intern.hub.infra.repository.user;

import com.intern.hub.core.repository.user.UserDirectoryRepository;
import com.intern.hub.infra.feign.HrmInternalFeignClient;
import com.intern.hub.library.common.exception.InternalErrorException;
import feign.FeignException;
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
}

