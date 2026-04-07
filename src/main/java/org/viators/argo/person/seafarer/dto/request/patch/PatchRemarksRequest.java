package org.viators.argo.person.seafarer.dto.request.patch;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.person.seafarer.SeafarerT;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchRemarksRequest {

    private JsonNullable<String> remarks = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(remarks, entity::setRemarks);
    }
}
