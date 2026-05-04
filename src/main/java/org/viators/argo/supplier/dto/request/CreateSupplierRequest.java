package org.viators.argo.supplier.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.viators.argo.supplier.SupplierT;

public record CreateSupplierRequest(
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Company name must be between 2-200 characters long")
    String companyName,

    @NotBlank(message = "Contact person is required")
    @Size(min = 3, max = 150, message = "Contact person must be between 3-150 characters long")
    String contactPerson,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    String email,

    @NotBlank(message = "Phone is required")
    @Size(max = 30, message = "Phone must be at most 30 characters long")
    @Pattern(regexp = "^\\+?[0-9 ()-]{6,30}$", message = "Phone must be a valid phone number")
    String phone,

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5-200 characters long")
    String address,

    @NotBlank(message = "VAT number is required")
    @Size(max = 50, message = "VAT number must be at most 50 characters long")
    String vatNumber
) {

    public SupplierT toEntity() {
        return SupplierT.builder()
            .companyName(companyName)
            .contactPerson(contactPerson)
            .email(email)
            .phone(phone)
            .address(address)
            .vatNumber(vatNumber)
            .build();
    }
}
