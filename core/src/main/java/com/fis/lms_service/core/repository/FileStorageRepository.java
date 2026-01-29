package com.fis.lms_service.core.repository;

import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 1/29/2026
 *
 **/
public interface FileStorageRepository {

    String uploadFile(MultipartFile file, String keyPrefix);

    String uploadFile(MultipartFile file, String keyPrefix, Long maxSizeBytes, String contentTypeRegex);

    void deleteFile(String key);

    String getPrivateUrl(String key);

}
