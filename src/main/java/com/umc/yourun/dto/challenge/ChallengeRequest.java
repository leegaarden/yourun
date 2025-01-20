package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class ChallengeRequest {

    @Schema(title = "CHALLENGE_REQ_01 : 크루 챌린지 생성 요청")
    public record CreateCrewChallengeReq(
            @Schema(description = "크루 이름", example = "거진홍길동")
            String crewName,

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate
    ) {}

    @Schema(title = "CHALLENGE_REQ_02 : 개인 챌린지 생성 요청 DTO")
    public record CreateSoloChallengeReq(

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate,

            @Schema(description = "챌린지 거리", example = "ONE_KM",
            title = "ONE_KM = 1, THREE_KM = 3, FIVE_KM = 5")
            ChallengeDistance challengeDistance
    ) {}
}
