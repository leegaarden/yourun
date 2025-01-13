package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class ChallengeRequest {

    @Schema(title = "CHALLENGE_REQ_01 : 크루 챌린지 생성 요청 DTO")
    public record CrewChallengeCreateReq(
            @Schema(description = "크루 이름", example = "동작구 사슴")
            String crewName,

            @Schema(description = "챌린지 시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate
    ) {}

    @Schema(title = "CHALLENGE_REQ_02 : 개인 챌린지 생성 요청 DTO")
    public record SoloChallengeCreateReq(
            @Schema(description = "챌린지 시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate,

            @Schema(description = "챌린지 거리", example = "KM1",
                    title = "KM1: 1KM, KM3: 3KM, KM5: 5KM")
            ChallengeDistance distance
    ) {}
}
