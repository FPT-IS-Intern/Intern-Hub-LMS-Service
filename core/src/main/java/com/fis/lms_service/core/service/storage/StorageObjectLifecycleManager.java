package com.fis.lms_service.core.service.storage;

import com.fis.lms_service.core.repository.FileStorageRepository;
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

  public void cleanupOnRollback(String key) {
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
              safeDelete(key);
            }
          }
        });
  }

  public void deleteAfterCommit(String key) {
    if (!hasText(key)) {
      return;
    }

    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      safeDelete(key);
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            safeDelete(key);
          }
        });
  }

  private void safeDelete(String key) {
    try {
      fileStorageRepository.deleteFile(key);
    } catch (Exception ex) {
      log.error("Failed to delete storage object key {}", key, ex);
    }
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
