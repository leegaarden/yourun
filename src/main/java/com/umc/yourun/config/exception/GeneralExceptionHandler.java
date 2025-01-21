package com.umc.yourun.config.exception;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.config.exception.custom.RunningException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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

    @ExceptionHandler(RunningException.class)
    public ApiResponse<Object> handleRunningException(RunningException e) {
        log.error("Running Exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.error(errorCode);
    }

    // 잘못된 PathParameters가 들어온 경우
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ApiResponse<Object> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error("Invalid PathParameters: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.INVALID_PATH_PARAMETER);



    // ENUM 타입에서 에러난 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid Enum Value: {}", e.getMessage());
        if (e.getMessage().contains("ChallengeDistance")) {
            return ApiResponse.error(ErrorCode.INVALID_CHALLENGE_DISTANCE);
        }
        return ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE);
    }

    //DTO 형식에서 에러난 경우
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ApiResponse<List<Map<String,String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Invalid DTO Value: {}", e.getMessage());
        List<Map<String, String>> fieldErrors = e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> Map.of(
                "field", fieldError.getField(),
                "message", Objects.requireNonNull(fieldError.getDefaultMessage())
            ))
            .toList();
        return ApiResponse.error("Validation failed", ErrorCode.INVALID_INPUT_VALUE,fieldErrors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage());

        // GeneralException인 경우
        if (e instanceof GeneralException) {
            GeneralException generalException = (GeneralException) e;
            return ApiResponse.error(generalException.getErrorCode());
        }

        // 그 외의 RuntimeException인 경우
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
