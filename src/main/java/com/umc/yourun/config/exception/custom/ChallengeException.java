package com.umc.yourun.config.exception.custom;

import ch.qos.logback.core.status.ErrorStatus;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import lombok.Getter;

@Getter
public class ChallengeException extends GeneralException {

    public ChallengeException(ErrorCode errorCode) {
        super(errorCode);
    }
}