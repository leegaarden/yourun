package com.umc.yourun.domain.enums;

import lombok.Getter;

@Getter
public enum ChallengeDistance {
    ONE_KM(1),
    THREE_KM(3),
    FIVE_KM(5);

    private final int distance;

    ChallengeDistance(int distance) {
        this.distance = distance;
    }

}
