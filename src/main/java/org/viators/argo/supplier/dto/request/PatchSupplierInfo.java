package org.viators.argo.supplier.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import org.viators.argo.supplier.SupplierT;

import static org.viators.argo.common.util.PatchUtils.applyIfPresent;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchSupplierInfo {

    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 2, max = 200, message = "Company name must be between 2-200 characters long")
    private JsonNullable<String> companyName = JsonNullable.undefined();

    @NotBlank(message = "Contact person cannot be blank")
    @Size(min = 3, max = 150, message = "Contact person must be between 3-150 characters long")
    private JsonNullable<String> contactPerson = JsonNullable.undefined();

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    private JsonNullable<String> email = JsonNullable.undefined();

    @NotBlank(message = "Phone cannot be blank")
    @Size(max = 30, message = "Phone must be at most 30 characters long")
    @Pattern(regexp = "^\\+?[0-9 ()-]{6,30}$", message = "Phone must be a valid phone number")
    private JsonNullable<String> phone = JsonNullable.undefined();

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 200, message = "Address must be between 5-200 characters long")
    private JsonNullable<String> address = JsonNullable.undefined();

    @NotNull(message = "Version field is required for updates")
    private Long version;

    public void update(SupplierT entity) {
        applyIfPresent(companyName, entity::setCompanyName);
        applyIfPresent(contactPerson, entity::setContactPerson);
        applyIfPresent(email, entity::setEmail);
        applyIfPresent(phone, entity::setPhone);
        applyIfPresent(address, entity::setAddress);
    }
}
