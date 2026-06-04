package org.viators.argo.docs.files.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.docs.files.DocumentFileT;

import java.time.Instant;

public record DocumentFileDetailsResponse(
    String publicId,
    String name,
    String description,
    String originalFilename,
    String contentType,
    Long fileSize,
    String docCategoryPublicId,
    String docCategoryName,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    Long version,
    ResourceStatusEnum status
) {

    public static DocumentFileDetailsResponse from(DocumentFileT entity) {
        return new DocumentFileDetailsResponse(
            entity.getPublicId(),
            entity.getName(),
            entity.getDescription(),
            entity.getOriginalFilename(),
            entity.getContentType(),
            entity.getFileSize(),
            entity.getDocCategory().getPublicId(),
            entity.getDocCategory().getName(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getVersion(),
            entity.getStatus()
        );
    }
}
