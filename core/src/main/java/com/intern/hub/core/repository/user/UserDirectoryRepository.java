package com.intern.hub.core.repository.user;

public interface UserDirectoryRepository {

    boolean existsByUserId(Long userId);
}

