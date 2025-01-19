package com.umc.yourun.config.exception;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.config.exception.custom.RunningException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

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

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handleRunningException(RunningException e) {
        log.error("Running Exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.error(errorCode);
    }



    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // ENUM 타입에서 에러난 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid Enum Value: {}", e.getMessage());
        if (e.getMessage().contains("ChallengeDistance")) {
            return ApiResponse.error(ErrorCode.INVALID_CHALLENGE_DISTANCE);
        }
        return ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE);
    }
}
