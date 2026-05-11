package com.intern.hub.core.repository;

import org.springframework.web.multipart.MultipartFile;

/** Admin 1/29/2026 */
public interface FileStorageRepository {

  String uploadFile(MultipartFile file, String keyPrefix, Long actorId);

  String uploadFile(
      MultipartFile file,
      String keyPrefix,
      Long actorId,
      Long maxSizeBytes,
      String contentTypeRegex);

  void deleteFile(String key, Long actorId);

  String getPrivateUrl(String key);
}
