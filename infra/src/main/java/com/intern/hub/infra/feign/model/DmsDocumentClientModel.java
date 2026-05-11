package com.intern.hub.infra.feign.model;

public record DmsDocumentClientModel(
    Long id,
    String objectKey,
    String originalFileName,
    String contentType,
    Long fileSize,
    Object status,
    Long actorId,
    Integer version,
    Object createdAt,
    Object updatedAt) {}
