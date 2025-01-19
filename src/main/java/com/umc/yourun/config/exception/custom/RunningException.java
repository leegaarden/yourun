package com.umc.yourun.config.exception.custom;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;

import lombok.Getter;

@Getter
public class RunningException extends GeneralException {
	public RunningException(ErrorCode errorCode) {
		super(errorCode);
	}
}
