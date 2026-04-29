package org.viators.argo.supplier;

import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.common.enums.ResourceStatusEnum;

public class SupplierSpecs {

    public static Specification<SupplierT> hasCompNameContaining(String companyNameContaining) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("companyName")), companyNameContaining.toLowerCase());
    }

    public static Specification<SupplierT> hasVatNumber(String vatNumber) {
        return (root, query, cb) ->
            cb.equal(root.get("vatNumber"), vatNumber);
    }

    public static Specification<SupplierT> hasEmail(String email) {
        return (root, query, cb) ->
            cb.equal(root.get("email"), email);
    }

    public static Specification<SupplierT> hasStatus(ResourceStatusEnum status) {
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }
}
