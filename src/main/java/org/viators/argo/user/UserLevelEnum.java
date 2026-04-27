package org.viators.argo.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserLevelEnum {
    LEVEL_1("", 1),
    LEVEL_2("", 2),
    LEVEL_3("", 3),
    LEVEL_4("", 4),
    LEVEL_5("", 5);

    private final String description;
    private final Integer ordinal;
}
