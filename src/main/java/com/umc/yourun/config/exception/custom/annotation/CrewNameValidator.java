package com.umc.yourun.config.exception.custom.annotation;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.repository.CrewChallengeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CrewNameValidator implements ConstraintValidator<ValidCrewName, String> {

    private final CrewChallengeRepository crewChallengeRepository;
    @Override
    public void initialize(ValidCrewName constraintAnnotation) {
        // 초기화가 필요한 경우 여기서 구현
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // 중복 검사
        if (crewChallengeRepository.existsByCrewNameIgnoreCase(value)) {
            throw new ChallengeException(ErrorCode.DUPLICATE_CREW_NAME);
        }

        // 공백 검사
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        // 길이 검사 (2-5자)
        if (value.length() < 2 || value.length() > 5) {
            return false;
        }

        // 한글과 공백만 포함하는지 검사
        return value.matches("^[가-힣]{2,5}$");
    }
}