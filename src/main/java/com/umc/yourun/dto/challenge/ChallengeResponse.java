package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public class ChallengeResponse {
    @Schema(title = "CHALLENGE_RES_01 : 크루 챌린지 생성 응답 DTO")
    public record CrewChallengeResult(
            @Schema(description = "챌린지 ID", example = "1")
            Long id,

            @Schema(description = "챌린지 시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate,

            @Schema(description = "크루 이름", example = "동작구 사슴")
            String crewName
    ) {}

    @Schema(title = "CHALLENGE_RES_02 : 개인 챌린지 생성 응답 DTO")
    public record SoloChallengeResult(
            @Schema(description = "챌린지 ID", example = "1")
            Long id,

            @Schema(description = "챌린지 거리", example = "KM3")
            ChallengeDistance distance,

            @Schema(description = "챌린지 시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "챌린지 종료일", example = "2025-01-17")
            LocalDate endDate
    ) {}
}