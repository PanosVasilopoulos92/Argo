package org.viators.argo.goodsreceipt;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.goodsreceipt.enums.GoodsReceiptStateEnum;
import org.viators.argo.goodsreceipt.enums.ReceiptLineFlagEnum;
import org.viators.argo.goodsreceipt.enums.ReceivedGoodsConditionEnum;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT;
import org.viators.argo.goodsreceipt.line.GoodsReceiptLineT_;
import org.viators.argo.purchaseorder.PurchaseOrderT_;
import org.viators.argo.supplier.SupplierT_;

import java.time.LocalDate;

public final class GoodsReceiptSpecs {

    public static Specification<GoodsReceiptT> hasReceiptNumber(String goodsReceiptNumber) {
        return (root, query, cb) ->
            cb.equal(root.get(GoodsReceiptT_.goodsReceiptNumber), goodsReceiptNumber);
    }

    public static Specification<GoodsReceiptT> hasPOPublicId(String poPublicId) {
        return (root, query, cb) ->
            cb.equal(
                root.get(GoodsReceiptT_.purchaseOrder).get(PurchaseOrderT_.publicId),
                poPublicId
            );
    }

    public static Specification<GoodsReceiptT> hasPONumber(String poNumber) {
        return (root, query, cb) ->
            cb.equal(
                root.get(GoodsReceiptT_.purchaseOrder).get(PurchaseOrderT_.purchaseOrderNumber),
                poNumber
            );
    }

    public static Specification<GoodsReceiptT> hasSupplierPublicId(String supplierPublicId) {
        return (root, query, cb) ->
            cb.equal(
                root.get(GoodsReceiptT_.purchaseOrder).get(PurchaseOrderT_.supplier).get(SupplierT_.publicId),
                supplierPublicId
            );
    }

    public static Specification<GoodsReceiptT> hasReceiptState(GoodsReceiptStateEnum receiptState) {
        return (root, query, cb) ->
            cb.equal(root.get(GoodsReceiptT_.receiptState), receiptState);
    }

    public static Specification<GoodsReceiptT> hasReceiptDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return (root, query, cb) -> {
            if (dateFrom != null && dateTo != null) {
                return cb.between(root.get(GoodsReceiptT_.receiptDate), dateFrom, dateTo);
            }
            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get(GoodsReceiptT_.receiptDate), dateFrom);
            }
            if (dateTo != null) {
                return cb.lessThanOrEqualTo(root.get(GoodsReceiptT_.receiptDate), dateTo);
            }

            return cb.conjunction();
        };
    }

    //  Filter for receipts containing any OVER_RECEIVED lines
    public static Specification<GoodsReceiptT> hasOverReceived() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<GoodsReceiptLineT> line = sub.from(GoodsReceiptLineT.class);
            sub.select(cb.literal(1L))
                .where(
                    cb.equal(line.get(GoodsReceiptLineT_.GOODS_RECEIPT), root),
                    cb.equal(line.get(GoodsReceiptLineT_.receiptLineFlag), ReceiptLineFlagEnum.OVER_RECEIVED)
                );

            return cb.exists(sub);
        };
    }

    //  Filter for receipts containing any non-OK condition lines
    public static Specification<GoodsReceiptT> hasDamagedOrWrongItem() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<GoodsReceiptLineT> line = sub.from(GoodsReceiptLineT.class);
            sub.select(cb.literal(1L))
                .where(
                    cb.equal(line.get("goodsReceipt"), root),
                    line.get("receivedGoodsCondition").in(
                        ReceivedGoodsConditionEnum.DAMAGED,
                        ReceivedGoodsConditionEnum.WRONG_ITEM,
                        ReceivedGoodsConditionEnum.OTHER
                    )
                );

            return cb.exists(sub);
        };
    }
}
