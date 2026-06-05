package org.viators.argo.docs.doccategory.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.docs.doccategory.DocCategoryT;
import org.viators.argo.docs.files.dto.response.DocumentFileSummaryResponse;

import java.time.Instant;
import java.util.List;

public record DocCategoryWithDocumentsResponse(
    String publicId,
    String name,
    String description,
    Instant createdAt,
    String createdBy,
    Long version,
    ResourceStatusEnum status,
    List<DocumentFileSummaryResponse> docFiles
) {

    public static DocCategoryWithDocumentsResponse from(
        DocCategoryT docCategory,
        List<DocumentFileSummaryResponse> docFiles
    ) {

        return new DocCategoryWithDocumentsResponse(
            docCategory.getPublicId(),
            docCategory.getName(),
            docCategory.getDescription(),
            docCategory.getCreatedAt(),
            docCategory.getCreatedBy(),
            docCategory.getVersion(),
            docCategory.getStatus(),
            docFiles
        );
    }

}
