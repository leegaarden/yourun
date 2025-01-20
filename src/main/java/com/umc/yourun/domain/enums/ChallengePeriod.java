package com.umc.yourun.domain.enums;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ChallengePeriod {
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int days;

    public static ChallengePeriod from(long days) {
        return Arrays.stream(values())
                .filter(period -> period.getDays() == days)
                .findFirst()
                .orElseThrow(() -> new ChallengeException(ErrorCode.INVALID_CHALLENGE_PERIOD));
    }
}
