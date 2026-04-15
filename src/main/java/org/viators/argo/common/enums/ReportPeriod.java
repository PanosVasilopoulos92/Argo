package org.viators.argo.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportPeriod {

    THIRTY(30),
    SIXTY(60),
    NINETY(90);

    private final int days;
}
