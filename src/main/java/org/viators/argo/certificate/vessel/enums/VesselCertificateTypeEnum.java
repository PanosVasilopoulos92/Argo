package org.viators.argo.certificate.vessel.enums;

import lombok.Getter;

@Getter
public enum VesselCertificateTypeEnum {
    SMC("Safety Management Certificate (ISM Code)"),
    ISSC("International Ship Security Certificate"),
    CERTIFICATE_OF_CLASS("Classification society certificate"),
    IOPP("International Oil Pollution Prevention Certificate"),
    SAFETY_EQUIPMENT("Cargo Ship Safety Equipment Certificate"),
    SAFETY_CONSTRUCTION("Cargo Ship Safety Construction Certificate"),
    SAFETY_RADIO("Cargo Ship Safety Radio Certificate"),
    LOAD_LINE("International Load Line Certificate"),
    TONNAGE("International Tonnage Certificate"),
    CLC("Civil Liability Convention Certificate (oil pollution)"),
    DMLC("Declaration of Maritime Labour Compliance"),
    OTHER("Catch-all for unlisted types");

    private final String description;

    VesselCertificateTypeEnum(String description) {
        this.description = description;
    }
}
