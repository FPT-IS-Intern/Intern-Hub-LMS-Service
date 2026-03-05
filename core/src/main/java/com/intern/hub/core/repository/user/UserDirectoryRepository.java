package com.intern.hub.core.repository.user;

import com.intern.hub.core.domain.model.user.UserDirectoryModel;
import java.util.List;
import java.util.Optional;

public interface UserDirectoryRepository {

    boolean existsByUserId(Long userId);

    Optional<UserDirectoryModel> findByEmail(String email);

    List<UserDirectoryModel> findByIds(List<Long> userIds);
}
