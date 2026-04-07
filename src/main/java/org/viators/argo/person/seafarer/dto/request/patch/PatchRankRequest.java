package org.viators.argo.person.seafarer.dto.request.patch;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchRankRequest {

    private JsonNullable<SeafarerRankEnum> rank = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(rank, entity::setRank);
    }
}
