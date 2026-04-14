package org.viators.argo.certificate.person;

import lombok.Getter;

@Getter
public enum PersonCertificateTypeEnum {
    COC("Certificate of Competency"),
    STCW_ENDORSEMENT("STCW Convention endorsement"),
    BST(" Basic Safety Training"),
    MEDICAL_FITNESS("Medical fitness certificate"),
    GMDSS("Global Maritime Distress and Safety System operator certificate"),
    FLAG_STATE_ENDORSEMENT("Flag state recognition of foreign certificate"),
    ADVANCED_FIREFIGHTING("Advanced fire fighting certificate"),
    MEDICAL_FIRST_AID("Medical first aid certificate"),
    SURVIVAL_CRAFT("Proficiency in survival craft"),
    SHIP_SECURITY_OFFICER("Ship Security Officer certificate"),
    OTHER("Catch-all for unlisted types");

    private final String description;

    PersonCertificateTypeEnum(String description) {
        this.description = description;
    }
}
