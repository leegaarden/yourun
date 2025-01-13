package com.umc.yourun.domain.enums;

import lombok.Getter;

@Getter
public enum ChallengeDistance {
    KM1("1KM"),
    KM3("3KM"),
    KM5("5KM");

    private final String value;

    ChallengeDistance(String value) {
        this.value = value;
    }
}
