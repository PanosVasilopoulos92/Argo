package org.viators.argo.item.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategoryEnum {

    ENGINE_SPARES("ENG"),
    DECK_STORES("DCK"),
    SAFETY_EQUIPMENT("SAF"),
    LUBRICANTS("LUB"),
    CHEMICALS("CHM"),
    PROVISIONS("PRV"),
    MEDICAL("MED"),
    ELECTRICAL("ELC"),
    NAVIGATION("NAV"),
    CABIN_STORES("CBN"),
    STATIONERY("STN"),
    OTHER("OTH");

    private final String itemCategoryCode;
}
