package com.umc.yourun.config.exception.custom.annotation;

import com.umc.yourun.repository.CrewChallengeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrewNameValidator implements ConstraintValidator<ValidCrewName, String> {

    private final CrewChallengeRepository crewChallengeRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 기본 유효성 검사
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        // 길이 검사 (2-5자)
        if (value.length() < 2 || value.length() > 5) {
            return false;
        }

        // 한글만 포함하는지 검사
        if (!value.matches("^[가-힣]{2,5}$")) {
            return false;
        }

        // 중복 검사
        return !crewChallengeRepository.existsByCrewNameIgnoreCase(value);
    }
}