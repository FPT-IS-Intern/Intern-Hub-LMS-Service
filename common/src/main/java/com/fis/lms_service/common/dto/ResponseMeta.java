package com.fis.lms_service.common.dto;

import java.time.Instant;

public record ResponseMeta(String requestId, String signature, long timestamp) {

  public static ResponseMeta fromRequestId(String requestId) {
    return new ResponseMeta(requestId, null, Instant.now().toEpochMilli());
  }
}
