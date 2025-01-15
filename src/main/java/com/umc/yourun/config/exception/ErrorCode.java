package com.umc.yourun.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 서버 에러
    INVALID_INPUT_VALUE(400, "S001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "S002", "서버 에러가 발생했습니다."),

    // Challenge 관련 에러
    INVALID_START_DATE(400, "C001", "시작일은 내일로 고정입니다."),
    INVALID_END_DATE(400, "C002","종료일은 시작일 이후여야 합니다."),
    INVALID_CHALLENGE_PERIOD(400, "C003","챌린지 기간은 최소 3일에서 최대 5일이어야 합니다."),
    INVALID_CREW_NAME_NULL(400, "C004","크루명은 필수입니다."),
    INVALID_CREW_NAME_FORMAT1(400, "C005","크루명은 특수문자를 포함할 수 없습니다."),
    INVALID_CREW_NAME_FORMAT2(400, "C006", "크루명은 공백 포함 3-5자 범위입니다."),
    INVALID_CHALLENGE_DISTANCE_NULL(400, "C007", "거리는 필수 입력값입니다."),
    INVALID_CHALLENGE_DISTANCE(400, "C008", "유효하지 않은 거리입니다. ONE_KM, THREE_KM, FIVE_KM 중 하나여야 합니다.");

    private final int status;
    private final String code;
    private final String message;

}
