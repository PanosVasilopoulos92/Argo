package org.viators.argo.purchaseorder;

import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderStateEnum;
import org.viators.argo.purchaseorder.enums.PurchaseOrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public class POSpecs {

    public static Specification<PurchaseOrderT> hasPONumber(String poNumber) {
        return (root, query, cb) ->
            cb.equal(root.get("purchaseOrderNumber"), poNumber);
    }

    public static Specification<PurchaseOrderT> hasSupplierCompanyNameContaining(String supCompanyName) {
        return (root, query, cb) ->
            cb.like(
                cb.lower(root.get("supplier").get("companyName")),
                "%" + supCompanyName.toLowerCase() + "%"
            );
    }

    public static Specification<PurchaseOrderT> hasReqPublicId(String reqPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get("requisition").get("publicId"), reqPublicId);
    }

    public static Specification<PurchaseOrderT> hasPOType(PurchaseOrderTypeEnum poType) {
        return (root, query, cb) ->
            cb.equal(root.get("purchaseOrderType"), poType);
    }

    public static Specification<PurchaseOrderT> hasPOState(PurchaseOrderStateEnum poState) {
        return (root, query, cb) ->
            cb.equal(root.get("purchaseOrderState"), poState);
    }

    public static Specification<PurchaseOrderT> hasCurrency(CurrencyEnum currency) {
        return (root, query, cb) ->
            cb.equal(root.get("currency"), currency);
    }

    public static Specification<PurchaseOrderT> hasSentDateRange(LocalDate sentAtFrom, LocalDate sentAtTo) {
        return (root, query, cb) -> {
            if (sentAtFrom != null && sentAtTo != null) {
                return cb.between(root.get("sentAt"), sentAtFrom, sentAtTo);
            }
            if (sentAtFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("sentAt"), sentAtFrom);
            }
            if (sentAtTo != null) {
                return cb.lessThanOrEqualTo(root.get("sentAt"), sentAtTo);
            }

            return cb.conjunction();
        };
    }

    public static Specification<PurchaseOrderT> hasTotalAmountRange(BigDecimal amountFrom, BigDecimal amountTo) {
        return (root, query, cb) -> {
            if (amountFrom != null && amountTo != null) {
                return cb.between(root.get("totalAmount"), amountFrom, amountTo);
            }
            if (amountFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("totalAmount"), amountFrom);
            }
            if (amountTo != null) {
                return cb.lessThanOrEqualTo(root.get("totalAmount"), amountTo);
            }

            return cb.conjunction();
        };
    }
}
