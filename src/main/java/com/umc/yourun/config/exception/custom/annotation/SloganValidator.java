package com.umc.yourun.config.exception.custom.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SloganValidator implements ConstraintValidator<ValidSlogan, String> {

    @Override
    public void initialize(ValidSlogan constraintAnnotation) {
        // 초기화가 필요한 경우 여기서 구현
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        // 길이 검사 (3-12자)
        if (value.length() < 3 || value.length() > 12) {
            return false;
        }

        // 한글과 공백만 포함하는지 검사
        return value.matches("^[가-힣\\s]+$");
    }
}