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

    INVALID_USER_NOTFOUND(400, "U001","해당 이메일을 가진 유저가 존재하지 않습니다."),
    INVALID_USER_INCONSISTENCY(400, "U002","비밀번호가 일치하지 않습니다."),

    INVALID_CREW_NAME_NULL(400, "C004","크루명은 필수입니다."),
    INVALID_CREW_NAME_FORMAT1(400, "C005","크루명은 한글로만 가능합니다."),
    INVALID_CREW_NAME_FORMAT2(400, "C006", "크루명은 공백없이 2-5자 범위입니다."),
    INVALID_CHALLENGE_DISTANCE_NULL(400, "C007", "거리는 필수 입력값입니다."),
    INVALID_CHALLENGE_DISTANCE(400, "C008", "유효하지 않은 거리입니다. ONE_KM, THREE_KM, FIVE_KM 중 하나여야 합니다."),
    CHALLENGE_NOT_FOUND(404, "C009", "존재하지 않는 챌린지입니다."),
    INVALID_CHALLENGE_STATUS(400, "C010", "이미 진행 중인 챌린지에는 참여할 수 없습니다."),
    CHALLENGE_EXPIRED(400, "C011", "만료된 챌린지입니다."),
    CANNOT_JOIN_OWN_CHALLENGE(400, "C012", "본인이 만든 챌린지에는 참여할 수 없습니다."),
    INVALID_CHALLENGE_CREATE(400, "C013", "종류별 하나의 챌린지만 생성할 수 있습니다."),
    INVALID_CHALLENGE_JOIN(400, "C014", "종류별 하나의 챌린지에만 참여할 수 있습니다."),
    CREW_CHALLENGE_FULL(400, "C015", "크루 인원이 모두 찼습니다."),
    DUPLICATE_CREW_NAME(400, "C016", "이미 사용 중인 크루명입니다."),
    NO_CREW_CHALLENGE_FOUND(400, "C017", "사용자는 크루에 참여하고 있지 않습니다."),

    //Running 관련 에러
    INVALID_END_TIME(400, "R001", "종료 시간은 시작 시간 이후여야 합니다."),
    RUNNING_DATA_NOT_FOUND(400, "R002", "존재하지 않는 러닝 데이터입니다."),

    // User 관련 에러
    INVALID_PATH_PARAMETER(400, "P001", "잘못된 PathParameter입니다."),
    USER_NOT_FOUND(400, "U001", "존재하지 않는 사용자입니다.");

    private final int status;
    private final String code;
    private final String message;

}
