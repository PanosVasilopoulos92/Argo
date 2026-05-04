package org.viators.argo.quotation;

import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.quotation.enums.QuotationStateEnum;

import java.time.LocalDate;

public class QuotationSpecs {

    public static Specification<QuotationT> hasPublicId(String publicId) {
        return (root, query, cb) ->
            cb.equal(root.get("publicId"), publicId);
    }

    public static Specification<QuotationT> hasRequisitionPublicId(String reqPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get("line").get("requisition").get("publicId"), reqPublicId);
    }

    public static Specification<QuotationT> hasState(QuotationStateEnum state) {
        return (root, query, cb) ->
            cb.equal(root.get("quotationState"), state);
    }

    public static Specification<QuotationT> hasValidDateRange(LocalDate validUntilFrom, LocalDate validUntilTo) {
        return (root, query, cb) -> {
            if (validUntilFrom != null && validUntilTo != null) {
                return cb.between(root.get("validUntil"), validUntilFrom, validUntilTo);
            }
            if (validUntilFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("validUntil"), validUntilFrom);
            }
            if (validUntilTo != null) {
                return cb.lessThanOrEqualTo(root.get("validUntil"), validUntilTo);
            }

            return cb.conjunction();
        };
    }

    public static Specification<QuotationT> hasNotExpired() {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("validUntil"), LocalDate.now());
    }
}
