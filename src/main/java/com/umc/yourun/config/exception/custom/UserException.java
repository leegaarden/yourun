package com.umc.yourun.config.exception.custom;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
