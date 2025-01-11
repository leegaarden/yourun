package com.umc.yourun.config.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int code;
    private final String message;

}