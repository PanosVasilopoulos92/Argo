package org.viators.argo.supplier.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.supplier.SupplierT;

import java.time.Instant;

public record SupplierDetailsResponse(
    String publicId,
    String companyName,
    String contactPersons,
    String email,
    String phone,
    String address,
    String vatNumber,
    ResourceStatusEnum status,
    Instant createdAt,
    Instant updatedAt,
    Long version
) {

    public static SupplierDetailsResponse from(SupplierT entity) {
        return new SupplierDetailsResponse(
            entity.getPublicId(),
            entity.getCompanyName(),
            entity.getContactPersons(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getAddress(),
            entity.getVatNumber(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
}
