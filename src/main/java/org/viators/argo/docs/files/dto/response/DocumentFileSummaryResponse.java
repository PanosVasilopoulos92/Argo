package org.viators.argo.docs.files.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.docs.files.DocumentFileT;

import java.time.Instant;

public record DocumentFileSummaryResponse(
    String publicId,
    String name,
    String description,
    String storageKey,
    String contentType,
    Long fileSize,
    Instant createdAt,
    ResourceStatusEnum status
) {

    public static DocumentFileSummaryResponse from(DocumentFileT documentFile) {
        return new DocumentFileSummaryResponse(
            documentFile.getPublicId(),
            documentFile.getName(),
            documentFile.getDescription(),
            documentFile.getStorageKey(),
            documentFile.getContentType(),
            documentFile.getFileSize(),
            documentFile.getCreatedAt(),
            documentFile.getStatus()
        );
    }
}
