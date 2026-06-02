package org.viators.argo.docs.doccategory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.viators.argo.docs.doccategory.DocCategoryT;

public record CreateDocCategoryRequest(

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters long")
    String name,

    @Size(max = 500, message = "Description must be at most 500 characters long")
    String description
) {

    public DocCategoryT toEntity() {
        return DocCategoryT.builder()
            .name(name)
            .description(description)
            .build();
    }
}
