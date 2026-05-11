package com.intern.hub.core.service.storage;

import com.intern.hub.core.repository.FileStorageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Coordinates external storage operations with DB transaction boundaries. */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageObjectLifecycleManager {

  FileStorageRepository fileStorageRepository;

  public void cleanupOnRollback(String key, Long actorId) {
    if (!hasText(key)) {
      return;
    }

    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == STATUS_ROLLED_BACK) {
              safeDelete(key, actorId);
            }
          }
        });
  }

  public void deleteAfterCommit(String key, Long actorId) {
    if (!hasText(key)) {
      return;
    }

    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      safeDelete(key, actorId);
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            safeDelete(key, actorId);
          }
        });
  }

  private void safeDelete(String key, Long actorId) {
    try {
      fileStorageRepository.deleteFile(key, actorId);
    } catch (Exception ex) {
      log.error("Failed to delete storage object key {}", key, ex);
    }
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
