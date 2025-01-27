package com.umc.yourun.config.exception.custom;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;

public class RankingException extends GeneralException {
    public RankingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
