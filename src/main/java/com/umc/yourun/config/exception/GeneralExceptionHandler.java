package com.umc.yourun.config.exception;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.config.exception.custom.RunningException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice(basePackages = "com.umc.yourun")  // actuator 패키지 제외
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

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, ValidationException.class})
    public ApiResponse<List<Map<String,String>>> handleValidationException(Exception e) {  // 파라미터 타입을 Exception으로 변경
        log.error("Invalid DTO Value: {}", e.getMessage());
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
            List<Map<String, String>> fieldErrors = validException.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> Map.of(
                            "field", fieldError.getField(),
                            "message", Objects.requireNonNull(fieldError.getDefaultMessage())
                    ))
                    .toList();
            return ApiResponse.error("Validation failed", ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
        }
        // ConstraintViolationException 처리
        return ApiResponse.error("Validation failed", ErrorCode.INVALID_INPUT_VALUE, List.of(Map.of("message", e.getMessage())));
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
    public ApiResponse<Object> handleException(Exception e, HttpServletRequest request) throws Exception {
        // actuator 요청인 경우 Spring의 기본 예외 처리로 위임
        if (request.getRequestURI().startsWith("/actuator")) {
            throw e;
        }

        log.error("Internal Server Error: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
