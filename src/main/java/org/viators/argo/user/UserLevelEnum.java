package org.viators.argo.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserLevelEnum {
    LEVEL_1(""),
    LEVEL_2(""),
    LEVEL_3(""),
    LEVEL_4(""),
    LEVEL_5("");

    private final String description;
}
