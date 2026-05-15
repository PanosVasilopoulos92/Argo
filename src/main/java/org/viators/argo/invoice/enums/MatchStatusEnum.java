package org.viators.argo.invoice.enums;

public enum MatchStatusEnum {
    MATCHED,
    PRICE_MISMATCH,
    QUANTITY_MISMATCH,
    BOTH_MISMATCH, // Quantity and price
    UNMATCHED
}
