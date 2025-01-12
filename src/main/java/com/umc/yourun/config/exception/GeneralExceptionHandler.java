package com.umc.yourun.config.exception;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.custom.ChallengeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 커스텀 익셉션 응답 구조 설정
@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(ChallengeException.class)
    public ApiResponse<Object> handleChallengeException(ChallengeException e) {
        log.error("Challenge Exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.error(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
