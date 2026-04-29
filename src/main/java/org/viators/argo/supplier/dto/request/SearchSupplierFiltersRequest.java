package org.viators.argo.supplier.dto.request;

import org.viators.argo.common.enums.ResourceStatusEnum;

public record SearchSupplierFiltersRequest(
    String companyNameContaining,
    String vatNumber,
    String email,
    ResourceStatusEnum status
) {
}
