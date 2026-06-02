package org.viators.argo.docs.doccategory.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.docs.doccategory.DocCategoryT;

import java.time.Instant;

public record DocCategoryDetailsResponse(
    String publicId,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    Long version,
    ResourceStatusEnum status
) {

    public static DocCategoryDetailsResponse from(DocCategoryT entity) {
        return new DocCategoryDetailsResponse(
            entity.getPublicId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getVersion(),
            entity.getStatus()
        );
    }
}
