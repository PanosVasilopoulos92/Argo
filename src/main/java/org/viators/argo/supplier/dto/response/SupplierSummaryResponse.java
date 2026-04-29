package org.viators.argo.supplier.dto.response;

import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.supplier.SupplierT;

import java.time.Instant;

public record SupplierSummaryResponse(
    String publicId,
    String companyName,
    String email,
    String vatNumber,
    Instant createdAt,
    ResourceStatusEnum status,
    Long version
) {

    public static SupplierSummaryResponse from(SupplierT supplier) {
        return new SupplierSummaryResponse(
            supplier.getPublicId(),
            supplier.getCompanyName(),
            supplier.getEmail(),
            supplier.getVatNumber(),
            supplier.getCreatedAt(),
            supplier.getStatus(),
            supplier.getVersion()
        );
    }
}
