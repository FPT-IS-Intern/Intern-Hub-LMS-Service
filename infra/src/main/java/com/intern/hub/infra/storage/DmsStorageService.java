package com.intern.hub.infra.storage;

import com.intern.hub.core.repository.FileStorageRepository;
import com.intern.hub.infra.feign.DmsInternalFeignClient;
import com.intern.hub.infra.feign.model.DmsDocumentClientModel;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.InternalErrorException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import feign.FeignException;

/**
 * Admin 1/29/2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DmsStorageService implements FileStorageRepository {

    DmsInternalFeignClient dmsInternalFeignClient;

    @NonFinal
    @Value("${services.dms.system-actor-id:0}")
    Long systemActorId;

    public String uploadFile(MultipartFile file, String keyPrefix) {
        return uploadFile(file, keyPrefix, 20 * 1024 * 1024L, ".*");
    }

    public String uploadFile(
            MultipartFile file, String keyPrefix,
            Long maxSizeBytes, String contentTypeRegex
    ) {

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
            ResponseApi<DmsDocumentClientModel> response =
                    dmsInternalFeignClient.uploadFile(file, keyPrefix, systemActorId);

            if (response == null || response.data() == null || !hasText(response.data().objectKey())) {
                throw new InternalErrorException(
                        "storage.upload.error", "DMS không trả về thông tin file sau khi upload");
            }

            return response.data().objectKey();
        } catch (Exception e) {
            log.error("DMS upload failed for destination path {}", keyPrefix, e);
            throw new InternalErrorException(
                    "storage.upload.error", "Không thể upload file lên hệ thống lưu trữ");
        }
    }

    public void deleteFile(String key) {
        try {
            dmsInternalFeignClient.deleteFile(key, systemActorId);
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
