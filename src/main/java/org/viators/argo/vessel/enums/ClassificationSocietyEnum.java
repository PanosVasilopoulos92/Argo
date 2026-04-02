package org.viators.argo.vessel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClassificationSocietyEnum {
    LR("Lloyd's Register"),
    DNV("Det Norske Veritas"),
    BV("Bureau Veritas"),
    ABS("American Bureau of Shipping"),
    CLASS_NK("Nippon Kaiji Kyokai"),
    RINA("Registro Italiano Navale"),
    CCS("China Classification Society"),
    KR("Korean Register"),
    IRS("Indian Register of Shipping"),
    PRS("Polish Register of Shipping");

    private final String fullName;

}
