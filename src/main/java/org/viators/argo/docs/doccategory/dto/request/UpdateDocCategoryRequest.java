package org.viators.argo.docs.doccategory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.docs.doccategory.DocCategoryT;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
public class UpdateDocCategoryRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters long")
    private JsonNullable<String> name = JsonNullable.undefined();

    @Size(max = 500, message = "Description must be at most 500 characters long")
    private JsonNullable<String> description = JsonNullable.undefined();

    @NotNull(message = "Version is required for updating resource")
    private Long version;

    public void update(DocCategoryT entity) {
        applyIfPresent(name, entity::setName);
        applyIfPresent(description, entity::setDescription);
    }
}
