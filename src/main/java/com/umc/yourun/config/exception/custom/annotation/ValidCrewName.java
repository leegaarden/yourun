package com.umc.yourun.config.exception.custom.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})            // 어노테이션이 적용될 수 있는 위치 지정
@Retention(RetentionPolicy.RUNTIME)     // 어노테이션 정보가 유지되는 범위 지정
@Constraint(validatedBy = CrewNameValidator.class)  // 유효성 검사를 실행할 클래스 지정
@Documented                             // JavaDoc에 문서화
public @interface ValidCrewName {
    // 유효성 검사 실패시 반환할 메시지
    String message() default "구호는 공백을 포함해서 한글로만 3-12자여야 합니다";

    // 유효성 검사가 실행될 그룹
    Class<?>[] groups() default {};

    // 추가 정보를 전달하는데 사용
    Class<? extends Payload>[] payload() default {};
}