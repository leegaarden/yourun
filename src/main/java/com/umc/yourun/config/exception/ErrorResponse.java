package com.umc.yourun.config.exception;

public record ErrorResponse(int status,String code, String message) {
}
