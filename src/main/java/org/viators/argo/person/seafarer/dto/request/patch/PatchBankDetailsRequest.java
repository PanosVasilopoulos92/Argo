package org.viators.argo.person.seafarer.dto.request.patch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.person.seafarer.SeafarerT;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchBankDetailsRequest {

    @Size(max = 60, message = "Bank name must be at most 60 characters long")
    private JsonNullable<String> bankName = JsonNullable.undefined();

    @Size(max = 20, message = "Bank account must be at most 20 characters long")
    private JsonNullable<String> bankAccount = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SeafarerT entity) {
        applyIfPresent(bankName, entity::setBankName);
        applyIfPresent(bankAccount, entity::setBankAccount);
    }
}
