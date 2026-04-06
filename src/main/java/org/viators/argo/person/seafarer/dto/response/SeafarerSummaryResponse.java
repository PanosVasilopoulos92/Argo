package org.viators.argo.person.seafarer.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.person.seafarer.SeafarerT;
import org.viators.argo.person.seafarer.enums.SeafarerRankEnum;

public record SeafarerSummaryResponse(
    String fullName,
    SeafarerRankEnum rank,
    String nationality,
    String passportNumber,
    ResourceStatusEnum status
) {

    public static SeafarerSummaryResponse from(SeafarerT entity) {
        return new SeafarerSummaryResponse(
            entity.getFirstName().concat(" ").concat(entity.getLastName()),
            entity.getRank(),
            entity.getNationality(),
            entity.getPassportNumber(),
            entity.getStatus()
        );
    }
}
