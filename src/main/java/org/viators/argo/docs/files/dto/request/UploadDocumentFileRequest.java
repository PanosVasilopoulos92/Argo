package org.viators.argo.docs.files.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UploadDocumentFileRequest(
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 60, message = "Display name must be between 2 and 60 characters long")
    String name,

    @Size(max = 400, message = "Description must be at most 400 characters long")
    String description,

    @NotBlank(message = "Category publicId is required")
    String docCategoryPublicId
) {
}
