package com.intern.hub.core.repository.user;

import com.intern.hub.core.domain.model.user.UserDirectoryModel;
import java.util.Optional;

public interface UserDirectoryRepository {

    boolean existsByUserId(Long userId);

    Optional<UserDirectoryModel> findByEmail(String email);
}
