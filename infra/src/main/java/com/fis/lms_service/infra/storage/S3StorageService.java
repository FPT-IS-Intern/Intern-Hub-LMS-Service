package com.fis.lms_service.infra.storage;

import com.fis.lms_service.core.repository.FileStorageRepository;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.InternalErrorException;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/** Admin 1/29/2026 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class S3StorageService implements FileStorageRepository {

  S3Client s3Client;
  S3Presigner s3Presigner;

  @NonFinal
  @Value("${aws.s3.bucket-name}")
  String bucketName;

  public String uploadFile(MultipartFile file, String keyPrefix) {
    return uploadFile(file, keyPrefix, 20 * 1024 * 1024L, ".*");
  }

  public String uploadFile(
      MultipartFile file, String keyPrefix, Long maxSizeBytes, String contentTypeRegex) {
    if (file.getSize() > maxSizeBytes) {
      throw new BadRequestException(
          "file.size.exceeded",
          "Dung lượng file vượt quá giới hạn " + (maxSizeBytes / 1024 / 1024) + "MB");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.matches(contentTypeRegex)) {
      throw new BadRequestException(
          "file.type.invalid", "Định dạng file không hợp lệ. Yêu cầu: " + contentTypeRegex);
    }

    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    String key = keyPrefix.endsWith("/") ? keyPrefix + fileName : keyPrefix + "/" + fileName;

    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromBytes(file.getBytes()));

      return key;
    } catch (Exception e) {
      log.error("S3 upload failed for key prefix {}", keyPrefix, e);
      throw new InternalErrorException(
          "storage.upload.error", "Không thể upload file lên hệ thống lưu trữ");
    }
  }

  public void deleteFile(String key) {
    try {
      s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    } catch (Exception e) {
      log.error("S3 delete failed for key {}", key, e);
      throw new InternalErrorException(
          "storage.delete.error", "Không thể delete file trong hệ thống lưu trữ");
    }
  }

  public String getPrivateUrl(String key) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(key).build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .getObjectRequest(getObjectRequest)
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}
