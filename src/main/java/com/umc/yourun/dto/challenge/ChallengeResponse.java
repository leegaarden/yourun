package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ChallengeResponse {
    @Getter
    @Schema(title = "CHALLENGE_RES_01 : 크루 챌린지 생성 응답 DTO")
    @Builder
    public static class CrewChallengeResult {
        @Schema(description = "챌린지 ID", example = "1")
        private Long id;

        @Schema(description = "챌린지 시작일", example = "2025-01-15")
        private LocalDate startDate;

        @Schema(description = "챌린지 종료일", example = "2025-01-22")
        private LocalDate endDate;

        @Schema(description = "크루 이름", example = "열정 러너")
        private String crewName;
    }

    @Getter
    @Schema(title = "CHALLENGE_RES_02 : 개인 챌린지 생성 응답 DTO")
    @Builder
    public static class SoloChallengeResult {
        @Schema(description = "챌린지 ID", example = "1")
        private Long id;

        @Schema(description = "챌린지 거리", example = "KM3")
        private ChallengeDistance distance;

        @Schema(description = "챌린지 시작일", example = "2025-01-15")
        private LocalDate startDate;

        @Schema(description = "챌린지 종료일", example = "2025-01-22")
        private LocalDate endDate;
    }
}