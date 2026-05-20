package org.viators.argo.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CurrencyEnum {
    EUR("€"),
    USD("$");

    private final String symbol;
}
