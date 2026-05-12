package com.intern.hub.infra.storage;

import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.infra.feign.DmsInternalFeignClient;
import com.intern.hub.infra.feign.model.DmsDocumentClientModel;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.InternalErrorException;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Admin 1/29/2026 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DmsStorageService implements FileStorageRepository {

  DmsInternalFeignClient dmsInternalFeignClient;

  @NonFinal
  @Value("${services.dms.system-actor-id:0}")
  Long systemActorId;

  public String uploadFile(MultipartFile file, String keyPrefix, Long actorId) {
    return uploadFile(file, keyPrefix, actorId, 20 * 1024 * 1024L, ".*");
  }

  public String uploadFile(
      MultipartFile file,
      String keyPrefix,
      Long actorId,
      Long maxSizeBytes,
      String contentTypeRegex) {

    log.info("Infra - Upload file to DMS request: keyPrefix={}, originalFileName={}, fileSize={}",
        keyPrefix,
        file.getOriginalFilename(),
        file.getSize());

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

    try {
      Long requestActorId = actorId != null ? actorId : systemActorId;
      ResponseApi<DmsDocumentClientModel> response =
          dmsInternalFeignClient.uploadFile(file, keyPrefix, requestActorId, false);

      if (response == null || response.data() == null || !hasText(response.data().objectKey())) {
        throw new InternalErrorException(
            "storage.upload.error", "DMS không trả về thông tin file sau khi upload");
      }

      log.info("Infra - Upload file to DMS response: keyPrefix={}, objectKey={}", keyPrefix, response.data().objectKey());
      return response.data().objectKey();
    } catch (Exception e) {
      log.error("DMS upload failed for destination path {}", keyPrefix, e);
      throw new InternalErrorException(
          "storage.upload.error", "Không thể upload file lên hệ thống lưu trữ");
    }
  }

  public void deleteFile(String key, Long actorId) {
    try {
      log.info("Infra - Delete file from DMS request: key={}", key);
      dmsInternalFeignClient.deleteFile(key, actorId != null ? actorId : systemActorId);
      log.info("Infra - Delete file from DMS response: key={}, result=success", key);
    } catch (FeignException.NotFound ex) {
      log.warn("DMS document not found when deleting key {}", key);
    } catch (Exception e) {
      log.error("DMS delete failed for key {}", key, e);
      throw new InternalErrorException(
          "storage.delete.error", "Không thể delete file trong hệ thống lưu trữ");
    }
  }

  public String getPrivateUrl(String key) {
    return key;
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
