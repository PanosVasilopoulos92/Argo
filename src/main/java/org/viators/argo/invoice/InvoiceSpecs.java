package org.viators.argo.invoice;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.common.enums.CurrencyEnum;
import org.viators.argo.invoice.enums.InvoiceStateEnum;
import org.viators.argo.invoice.enums.MatchStatusEnum;
import org.viators.argo.invoice.line.InvoiceLineT;
import org.viators.argo.invoice.line.InvoiceLineT_;
import org.viators.argo.purchaseorder.PurchaseOrderT_;
import org.viators.argo.supplier.SupplierT_;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceSpecs {

    public static Specification<InvoiceT> hasInvoiceNumber(String invoiceNumber) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.invoiceNumber), invoiceNumber);
    }

    public static Specification<InvoiceT> hasSupplierInvoiceReference(String supplierInvoiceReference) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.supplierInvoiceReference), supplierInvoiceReference);
    }

    public static Specification<InvoiceT> hasSupplierPublicId(String supplierPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.supplier).get(SupplierT_.publicId), supplierPublicId);
    }

    public static Specification<InvoiceT> hasPOPublicId(String poPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.purchaseOrder).get(PurchaseOrderT_.publicId), poPublicId);
    }

    public static Specification<InvoiceT> hasInvoiceState(InvoiceStateEnum invoiceState) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.invoiceState), invoiceState);
    }

    public static Specification<InvoiceT> hasCurrency(CurrencyEnum currency) {
        return (root, query, cb) ->
            cb.equal(root.get(InvoiceT_.currency), currency);
    }

    public static Specification<InvoiceT> hasInvoiceDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return (root, query, cb) -> {
            if (dateFrom != null && dateTo != null) {
                return cb.between(root.get(InvoiceT_.invoiceDate), dateFrom, dateTo);
            }
            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get(InvoiceT_.invoiceDate), dateFrom);
            }
            if (dateTo != null) {
                return cb.lessThanOrEqualTo(root.get(InvoiceT_.invoiceDate), dateTo);
            }

            return cb.conjunction();
        };
    }

    public static Specification<InvoiceT> hasTotalAmountRange(BigDecimal amountFrom, BigDecimal amountTo) {
        return (root, query, cb) -> {
            if (amountFrom != null && amountTo != null) {
                return cb.between(root.get(InvoiceT_.totalAmount), amountFrom, amountTo);
            }
            if (amountFrom != null) {
                return cb.greaterThanOrEqualTo(root.get(InvoiceT_.totalAmount), amountFrom);
            }
            if (amountTo != null) {
                return cb.lessThanOrEqualTo(root.get(InvoiceT_.totalAmount), amountTo);
            }

            return cb.conjunction();
        };
    }

    public static Specification<InvoiceT> hasDueDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return (root, query, cb) -> {
            if (dateFrom != null && dateTo != null) {
                return cb.between(root.get(InvoiceT_.invoiceDueDate), dateFrom, dateTo);
            }
            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get(InvoiceT_.invoiceDueDate), dateFrom);
            }
            if (dateTo != null) {
                return cb.lessThanOrEqualTo(root.get(InvoiceT_.invoiceDueDate), dateTo);
            }

            return cb.conjunction();
        };
    }

    public static Specification<InvoiceT> hasUnmatchedLines() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<InvoiceLineT> line = sub.from(InvoiceLineT.class);
            sub.select(cb.literal(1L))
                .where(
                    cb.equal(line.get(InvoiceLineT_.invoice), root),
                    cb.equal(line.get(InvoiceLineT_.matchStatus), MatchStatusEnum.UNMATCHED)
                );

            return cb.exists(sub);
        };
    }

    public static Specification<InvoiceT> hasPriceDiscrepancy() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<InvoiceLineT> line = sub.from(InvoiceLineT.class);
            sub.select(cb.literal(1L))
                .where(
                    cb.equal(line.get(InvoiceLineT_.invoice), root),
                    line.get(InvoiceLineT_.matchStatus).in(List.of(MatchStatusEnum.PRICE_MISMATCH, MatchStatusEnum.BOTH_MISMATCH))
                );

            return cb.exists(sub);
        };
    }

    public static Specification<InvoiceT> hasQuantityDiscrepancy() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<InvoiceLineT> line = sub.from(InvoiceLineT.class);
            sub.select(cb.literal(1L))
                .where(
                    cb.equal(line.get(InvoiceLineT_.invoice), root),
                    line.get(InvoiceLineT_.matchStatus).in(List.of(MatchStatusEnum.QUANTITY_MISMATCH, MatchStatusEnum.BOTH_MISMATCH))
                );

            return cb.exists(sub);
        };
    }

}
